package com.elanor.checkout.service;

import com.elanor.cart.dto.CartDto;
import com.elanor.cart.dto.CartItemDto;
import com.elanor.cart.entity.Cart;
import com.elanor.cart.entity.CartItem;
import com.elanor.cart.service.CartService;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.checkout.dto.*;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.idempotency.service.IdempotencyService;
import com.elanor.coupon.dto.CouponValidationResponse;
import com.elanor.coupon.service.CouponService;
import com.elanor.customer.entity.Address;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.AddressRepository;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.inventory.entity.Inventory;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.dto.OrderAddressDto;
import com.elanor.order.dto.OrderDto;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderAddress;
import com.elanor.order.entity.OrderItem;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CartService cartService;
    private final CouponService couponService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final AddressRepository addressRepository;
    private final IdempotencyService idempotencyService;

    private final BigDecimal freeShippingThreshold;
    private final BigDecimal shippingCharge;
    private final int estimatedMinDays;
    private final int estimatedMaxDays;

    public CheckoutService(
            CartService cartService,
            CouponService couponService,
            InventoryService inventoryService,
            OrderRepository orderRepository,
            CustomerProfileRepository customerProfileRepository,
            AddressRepository addressRepository,
            IdempotencyService idempotencyService,
            @Value("${elanor.shipping.free-threshold:1500.00}") BigDecimal freeShippingThreshold,
            @Value("${elanor.shipping.below-threshold-charge:99.00}") BigDecimal shippingCharge,
            @Value("${elanor.shipping.estimated-min-days:3}") int estimatedMinDays,
            @Value("${elanor.shipping.estimated-max-days:7}") int estimatedMaxDays) {
        this.cartService = cartService;
        this.couponService = couponService;
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.addressRepository = addressRepository;
        this.idempotencyService = idempotencyService;
        this.freeShippingThreshold = freeShippingThreshold;
        this.shippingCharge = shippingCharge;
        this.estimatedMinDays = estimatedMinDays;
        this.estimatedMaxDays = estimatedMaxDays;
    }

    @Transactional(readOnly = true)
    public ShippingQuoteResponse getShippingQuote(ShippingQuoteRequest request, UUID userId, String guestToken) {
        BigDecimal subtotal = request != null && request.getSubtotal() != null
                ? request.getSubtotal()
                : cartService.getCartDto(userId, guestToken).getSubtotal();

        boolean eligibleForFreeShipping = subtotal.compareTo(freeShippingThreshold) >= 0;
        BigDecimal calculatedShipping = (subtotal.compareTo(BigDecimal.ZERO) > 0 && !eligibleForFreeShipping)
                ? shippingCharge
                : BigDecimal.ZERO;

        BigDecimal amountNeeded = freeShippingThreshold.subtract(subtotal);
        if (amountNeeded.compareTo(BigDecimal.ZERO) < 0) {
            amountNeeded = BigDecimal.ZERO;
        }

        String description = String.format("Estimated delivery in %d-%d business days", estimatedMinDays, estimatedMaxDays);

        return new ShippingQuoteResponse(
                calculatedShipping,
                freeShippingThreshold,
                eligibleForFreeShipping,
                amountNeeded,
                estimatedMinDays,
                estimatedMaxDays,
                description
        );
    }

    @Transactional(readOnly = true)
    public CheckoutValidateResponse validateCheckout(CheckoutValidateRequest request, UUID userId, String guestToken) {
        CheckoutValidateResponse response = new CheckoutValidateResponse();
        List<String> errors = new ArrayList<>();

        Cart cart = cartService.getOrCreateCart(userId, guestToken);
        CartDto cartDto = cartService.mapToDto(cart);

        if (cart.getItems().isEmpty()) {
            errors.add("Your cart is empty. Please add items before checking out.");
        }

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant == null || !variant.isActive()) {
                errors.add("Item " + (variant != null ? variant.getSku() : "unknown") + " is no longer active.");
                continue;
            }

            if (variant.getProduct() != null && !variant.getProduct().isCurrentlyPublishable()) {
                errors.add("Product " + variant.getProduct().getName() + " is currently unavailable for purchase.");
                continue;
            }

            Inventory inventory = inventoryService.getOrCreateInventory(variant);
            if (inventory.getAvailable() < item.getQuantity()) {
                errors.add(String.format("Insufficient stock for %s (%s). Available: %d, in cart: %d",
                        variant.getProduct() != null ? variant.getProduct().getName() : variant.getSku(),
                        variant.getName() != null ? variant.getName() : variant.getSku(),
                        inventory.getAvailable(),
                        item.getQuantity()));
            }
        }

        // Validate Address
        OrderAddressDto validatedAddress = resolveAndValidateAddress(request, userId, errors);

        // Validate Coupon
        String couponCodeToUse = request != null && request.getCouponCode() != null && !request.getCouponCode().isBlank()
                ? request.getCouponCode()
                : cart.getAppliedCouponCode();

        BigDecimal discount = BigDecimal.ZERO;
        String appliedCode = null;

        if (couponCodeToUse != null && !couponCodeToUse.isBlank() && cartDto.getSubtotal().compareTo(BigDecimal.ZERO) > 0) {
            CouponValidationResponse couponValidation = couponService.validateCoupon(couponCodeToUse, cartDto.getSubtotal());
            if (couponValidation.isValid()) {
                discount = couponValidation.getDiscountAmount();
                appliedCode = couponValidation.getCode();
            } else {
                errors.add("Coupon error: " + couponValidation.getMessage());
            }
        }

        BigDecimal subtotal = cartDto.getSubtotal();
        boolean eligibleForFreeShipping = subtotal.compareTo(freeShippingThreshold) >= 0;
        BigDecimal calculatedShipping = (subtotal.compareTo(BigDecimal.ZERO) > 0 && !eligibleForFreeShipping)
                ? shippingCharge
                : BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount).add(calculatedShipping).add(tax);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        response.setValid(errors.isEmpty());
        response.setErrors(errors);
        response.setItems(cartDto.getItems());
        response.setSubtotal(subtotal);
        response.setDiscountAmount(discount);
        response.setShippingCharge(calculatedShipping);
        response.setTaxAmount(tax);
        response.setTotalAmount(total);
        response.setAppliedCouponCode(appliedCode);
        response.setEligibleForFreeShipping(eligibleForFreeShipping);
        response.setEstimatedDeliveryMinDays(estimatedMinDays);
        response.setEstimatedDeliveryMaxDays(estimatedMaxDays);
        response.setValidatedAddress(validatedAddress);

        return response;
    }

    @Transactional
    public CheckoutResponse createOrder(CreateOrderRequest request, UUID userId, String guestToken, String idempotencyKey) {
        // 1. Idempotency Cache Check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<CheckoutResponse> cached = idempotencyService.getCachedResponse(idempotencyKey, CheckoutResponse.class);
            if (cached.isPresent()) {
                log.info("Returning cached order response for idempotency key [{}]", idempotencyKey);
                return cached.get();
            }
        }

        // 2. Validate Checkout
        CheckoutValidateRequest validateReq = new CheckoutValidateRequest(
                request != null ? request.getAddressId() : null,
                request != null ? request.getDeliveryAddress() : null,
                request != null ? request.getCouponCode() : null
        );

        CheckoutValidateResponse validation = validateCheckout(validateReq, userId, guestToken);
        if (!validation.isValid()) {
            String firstError = !validation.getErrors().isEmpty() ? validation.getErrors().get(0) : "Checkout validation failed.";
            throw new BusinessException(ErrorCode.CHECKOUT_INVALID, firstError);
        }

        Cart cart = cartService.getOrCreateCart(userId, guestToken);
        CustomerProfile customer = null;
        if (userId != null) {
            customer = customerProfileRepository.findByUserId(userId)
                    .orElse(null);
        }

        // 3. Generate Unique Order Number
        String orderNumber = generateOrderNumber();

        // 4. Create Order Entity
        Order order = new Order(
                orderNumber,
                customer,
                validation.getSubtotal(),
                validation.getDiscountAmount(),
                validation.getShippingCharge(),
                validation.getTaxAmount(),
                validation.getTotalAmount(),
                validation.getAppliedCouponCode(),
                validation.getEstimatedDeliveryMinDays(),
                validation.getEstimatedDeliveryMaxDays(),
                request != null ? request.getNotes() : null
        );

        // 5. Create Order Address Snapshot
        OrderAddressDto addrDto = validation.getValidatedAddress();
        OrderAddress orderAddress = new OrderAddress(
                order,
                addrDto.getFullName(),
                addrDto.getPhone(),
                addrDto.getAddressLine1(),
                addrDto.getAddressLine2(),
                addrDto.getCity(),
                addrDto.getState(),
                addrDto.getPostalCode(),
                addrDto.getCountry()
        );
        order.setAddress(orderAddress);

        // 6. Create Order Items Snapshot
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            BigDecimal unitPrice = variant.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem(
                    order,
                    variant,
                    variant.getProduct() != null ? variant.getProduct().getName() : "Product",
                    variant.getSku(),
                    variant.getName(),
                    unitPrice,
                    cartItem.getQuantity(),
                    lineTotal
            );
            order.addItem(orderItem);
        }

        // 7. Add Initial Status History
        String actor = customer != null ? ("CUSTOMER:" + customer.getId()) : "GUEST";
        order.addStatusHistory(null, OrderStatus.CREATED, "Order created successfully via checkout", actor);

        // Save Order
        Order savedOrder = orderRepository.save(order);

        // 8. Reserve Inventory for all ordered items
        for (OrderItem item : savedOrder.getItems()) {
            if (item.getVariant() != null) {
                inventoryService.reserveStock(item.getVariant(), savedOrder.getId(), item.getQuantity());
            }
        }

        // 9. Increment Coupon Usage
        if (savedOrder.getAppliedCouponCode() != null && !savedOrder.getAppliedCouponCode().isBlank()) {
            couponService.incrementUsage(savedOrder.getAppliedCouponCode());
        }

        // 10. Clear Cart
        cartService.clearCart(cart);

        String paymentMethod = request != null && request.getPaymentMethod() != null ? request.getPaymentMethod() : "DEMO_ONLINE";
        OrderDto orderDto = new OrderDto(savedOrder);
        CheckoutResponse response = new CheckoutResponse(orderDto, true, paymentMethod, "Order placed successfully. Please proceed to payment.");

        // 11. Save Idempotency Record
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveRecord(idempotencyKey, "ORDER", savedOrder.getId().toString(), response, 201);
        }

        log.info("Successfully placed order [{}] with total ₹{}", savedOrder.getOrderNumber(), savedOrder.getTotalAmount());
        return response;
    }

    private OrderAddressDto resolveAndValidateAddress(CheckoutValidateRequest request, UUID userId, List<String> errors) {
        if (request != null && request.getAddressId() != null) {
            Optional<Address> addressOpt = addressRepository.findById(request.getAddressId());
            if (addressOpt.isEmpty()) {
                errors.add("Selected delivery address was not found.");
                return null;
            }
            Address addr = addressOpt.get();
            if (userId != null && (addr.getCustomer() == null || addr.getCustomer().getUser() == null ||
                    !addr.getCustomer().getUser().getId().equals(userId))) {
                errors.add("Selected address does not belong to the current customer.");
                return null;
            }
            return new OrderAddressDto(
                    addr.getFullName(),
                    addr.getPhone(),
                    addr.getAddressLine1(),
                    addr.getAddressLine2(),
                    addr.getCity(),
                    addr.getState(),
                    addr.getPostalCode(),
                    addr.getCountry()
            );
        }

        if (request != null && request.getDeliveryAddress() != null) {
            CheckoutDeliveryAddressDto dto = request.getDeliveryAddress();
            if (dto.getFullName() == null || dto.getFullName().isBlank()) {
                errors.add("Full name is required for delivery.");
            }
            if (dto.getPhone() == null || dto.getPhone().isBlank()) {
                errors.add("Phone number is required for delivery.");
            }
            if (dto.getAddressLine1() == null || dto.getAddressLine1().isBlank()) {
                errors.add("Address line 1 is required.");
            }
            if (dto.getCity() == null || dto.getCity().isBlank()) {
                errors.add("City is required.");
            }
            if (dto.getState() == null || dto.getState().isBlank()) {
                errors.add("State is required.");
            }
            if (dto.getPostalCode() == null || dto.getPostalCode().isBlank()) {
                errors.add("Postal code is required.");
            }

            return new OrderAddressDto(
                    dto.getFullName(),
                    dto.getPhone(),
                    dto.getAddressLine1(),
                    dto.getAddressLine2(),
                    dto.getCity(),
                    dto.getState(),
                    dto.getPostalCode(),
                    dto.getCountry()
            );
        }

        // If authenticated and no address provided, try default address
        if (userId != null) {
            Optional<CustomerProfile> profileOpt = customerProfileRepository.findByUserId(userId);
            if (profileOpt.isPresent()) {
                CustomerProfile customer = profileOpt.get();
                Optional<Address> defaultAddrOpt = addressRepository.findByCustomerAndIsDefaultTrue(customer);
                Address addr = defaultAddrOpt.orElseGet(() -> {
                    List<Address> addresses = addressRepository.findByCustomer(customer);
                    return addresses.isEmpty() ? null : addresses.get(0);
                });

                if (addr != null) {
                    return new OrderAddressDto(
                            addr.getFullName(),
                            addr.getPhone(),
                            addr.getAddressLine1(),
                            addr.getAddressLine2(),
                            addr.getCity(),
                            addr.getState(),
                            addr.getPostalCode(),
                            addr.getCountry()
                    );
                }
            }
        }

        errors.add("A valid delivery address is required to proceed with checkout.");
        return null;
    }

    private String generateOrderNumber() {
        long timestamp = System.currentTimeMillis() % 100000000L;
        int randomPart = 1000 + RANDOM.nextInt(9000);
        return "ELN-" + timestamp + "-" + randomPart;
    }
}
