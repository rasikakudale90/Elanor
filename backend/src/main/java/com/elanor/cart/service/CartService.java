package com.elanor.cart.service;

import com.elanor.cart.dto.*;
import com.elanor.cart.entity.Cart;
import com.elanor.cart.entity.CartItem;
import com.elanor.cart.repository.CartItemRepository;
import com.elanor.cart.repository.CartRepository;
import com.elanor.catalog.entity.ProductMedia;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.inventory.entity.Inventory;
import com.elanor.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryService inventoryService;
    private final com.elanor.coupon.service.CouponService couponService;

    private final BigDecimal freeShippingThreshold;
    private final BigDecimal shippingCharge;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CustomerProfileRepository customerProfileRepository,
            ProductVariantRepository productVariantRepository,
            InventoryService inventoryService,
            com.elanor.coupon.service.CouponService couponService,
            @Value("${elanor.shipping.free-threshold:1500.00}") BigDecimal freeShippingThreshold,
            @Value("${elanor.shipping.below-threshold-charge:99.00}") BigDecimal shippingCharge) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryService = inventoryService;
        this.couponService = couponService;
        this.freeShippingThreshold = freeShippingThreshold;
        this.shippingCharge = shippingCharge;
    }

    @Transactional
    public Cart getOrCreateCart(UUID userId, String guestToken) {
        if (userId != null) {
            CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

            return cartRepository.findByCustomer(customer)
                    .orElseGet(() -> cartRepository.save(new Cart(customer)));
        } else if (guestToken != null && !guestToken.isBlank()) {
            return cartRepository.findByGuestToken(guestToken)
                    .orElseGet(() -> cartRepository.save(new Cart(guestToken)));
        } else {
            String newToken = UUID.randomUUID().toString();
            return cartRepository.save(new Cart(newToken));
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(UUID userId, String guestToken) {
        Cart cart = getOrCreateCart(userId, guestToken);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto addItem(UUID userId, String guestToken, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId, guestToken);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        if (!variant.isActive() || (variant.getProduct() != null && !variant.getProduct().isCurrentlyPublishable())) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ACTIVE);
        }

        Inventory inventory = inventoryService.getOrCreateInventory(variant);
        int availableStock = inventory.getAvailable();

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndVariant(cart, variant);
        int targetQty = request.getQuantity();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            targetQty += item.getQuantity();
            if (targetQty > availableStock) {
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Cannot add quantity. Available stock: " + availableStock);
            }
            item.setQuantity(targetQty);
            cartItemRepository.save(item);
        } else {
            if (targetQty > availableStock) {
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Requested quantity exceeds available stock (" + availableStock + ").");
            }
            CartItem newItem = new CartItem(cart, variant, targetQty);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        return mapToDto(cart);
    }

    @Transactional
    public CartDto updateItem(UUID userId, String guestToken, UUID variantId, int quantity) {
        Cart cart = getOrCreateCart(userId, guestToken);
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        CartItem item = cartItemRepository.findByCartAndVariant(cart, variant)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            cart.getItems().remove(item);
        } else {
            Inventory inventory = inventoryService.getOrCreateInventory(variant);
            if (quantity > inventory.getAvailable()) {
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Requested quantity exceeds available stock (" + inventory.getAvailable() + ").");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return mapToDto(cart);
    }

    @Transactional
    public CartDto removeItem(UUID userId, String guestToken, UUID variantId) {
        Cart cart = getOrCreateCart(userId, guestToken);
        cartItemRepository.deleteByCartAndVariantId(cart, variantId);
        cart.getItems().removeIf(item -> item.getVariant().getId().equals(variantId));
        return mapToDto(cart);
    }

    @Transactional
    public CartDto mergeCart(UUID userId, String guestToken) {
        if (guestToken == null || guestToken.isBlank()) {
            return getCartDto(userId, null);
        }

        CustomerProfile customer = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found."));

        Cart customerCart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(new Cart(customer)));

        Optional<CartItem> guestCartOpt = cartRepository.findByGuestToken(guestToken).stream()
                .findFirst().map(c -> c.getItems().isEmpty() ? null : c.getItems().get(0));

        Optional<Cart> guestCartEntityOpt = cartRepository.findByGuestToken(guestToken);
        if (guestCartEntityOpt.isPresent()) {
            Cart guestCart = guestCartEntityOpt.get();

            for (CartItem guestItem : guestCart.getItems()) {
                ProductVariant variant = guestItem.getVariant();
                if (!variant.isActive() || (variant.getProduct() != null && !variant.getProduct().isCurrentlyPublishable())) {
                    continue; // Skip inactive/unpublished items
                }

                Inventory inventory = inventoryService.getOrCreateInventory(variant);
                int availableStock = inventory.getAvailable();
                if (availableStock <= 0) {
                    continue; // Skip out-of-stock items
                }

                Optional<CartItem> customerItemOpt = cartItemRepository.findByCartAndVariant(customerCart, variant);
                if (customerItemOpt.isPresent()) {
                    CartItem custItem = customerItemOpt.get();
                    int mergedQty = custItem.getQuantity() + guestItem.getQuantity();
                    // Cap quantity to available stock per SRS rules (no overselling)
                    int finalQty = Math.min(mergedQty, availableStock);
                    custItem.setQuantity(finalQty);
                    cartItemRepository.save(custItem);
                } else {
                    int finalQty = Math.min(guestItem.getQuantity(), availableStock);
                    CartItem newItem = new CartItem(customerCart, variant, finalQty);
                    cartItemRepository.save(newItem);
                    customerCart.getItems().add(newItem);
                }
            }

            cartRepository.delete(guestCart);
        }

        return mapToDto(customerCart);
    }

    @Transactional
    public CartDto applyCoupon(UUID userId, String guestToken, String couponCode) {
        Cart cart = getOrCreateCart(userId, guestToken);
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Cannot apply coupon to an empty cart.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        com.elanor.coupon.dto.CouponValidationResponse validation = couponService.validateCoupon(couponCode, subtotal);
        if (!validation.isValid()) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, validation.getMessage());
        }

        cart.setAppliedCouponCode(validation.getCode());
        cartRepository.save(cart);

        return mapToDto(cart);
    }

    @Transactional
    public CartDto removeCoupon(UUID userId, String guestToken) {
        Cart cart = getOrCreateCart(userId, guestToken);
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
    }

    public CartDto mapToDto(Cart cart) {
        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            Inventory inv = inventoryService.getOrCreateInventory(variant);

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
            totalQuantity += item.getQuantity();

            String imageUrl = variant.getProduct() != null && variant.getProduct().getMedia() != null
                    ? variant.getProduct().getMedia().stream().filter(ProductMedia::isCover).findFirst()
                    .map(ProductMedia::getMediaUrl).orElse(null)
                    : null;

            itemDtos.add(new CartItemDto(
                    item.getId(),
                    variant.getId(),
                    variant.getSku(),
                    variant.getProduct() != null ? variant.getProduct().getName() : "",
                    variant.getProduct() != null ? variant.getProduct().getSlug() : "",
                    variant.getName(),
                    variant.getPrice(),
                    item.getQuantity(),
                    lineTotal,
                    imageUrl,
                    inv.getAvailable()
            ));
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (cart.getAppliedCouponCode() != null && !cart.getAppliedCouponCode().isBlank() && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            com.elanor.coupon.dto.CouponValidationResponse validation = couponService.validateCoupon(cart.getAppliedCouponCode(), subtotal);
            if (validation.isValid()) {
                discount = validation.getDiscountAmount();
            } else {
                // Auto-clear invalid/expired coupon
                cart.setAppliedCouponCode(null);
                cartRepository.save(cart);
            }
        }

        boolean eligibleForFreeShipping = subtotal.compareTo(freeShippingThreshold) >= 0;
        BigDecimal calculatedShipping = (subtotal.compareTo(BigDecimal.ZERO) > 0 && !eligibleForFreeShipping)
                ? shippingCharge
                : BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal finalTotal = subtotal.subtract(discount).add(calculatedShipping).add(tax);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setItems(itemDtos);
        dto.setTotalQuantity(totalQuantity);
        dto.setSubtotal(subtotal);
        dto.setDiscount(discount);
        dto.setShippingCharge(calculatedShipping);
        dto.setTax(tax);
        dto.setTotal(finalTotal);
        dto.setAppliedCouponCode(cart.getAppliedCouponCode());
        dto.setEligibleForFreeShipping(eligibleForFreeShipping);
        dto.setFreeShippingThreshold(freeShippingThreshold);

        return dto;
    }
}
