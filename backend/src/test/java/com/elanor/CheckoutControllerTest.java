package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.cart.dto.AddToCartRequest;
import com.elanor.cart.dto.ApplyCartCouponRequest;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.checkout.dto.CheckoutDeliveryAddressDto;
import com.elanor.checkout.dto.CheckoutValidateRequest;
import com.elanor.checkout.dto.CreateOrderRequest;
import com.elanor.checkout.dto.ShippingQuoteRequest;
import com.elanor.coupon.dto.CreateCouponRequest;
import com.elanor.coupon.entity.DiscountType;
import com.elanor.coupon.service.CouponService;
import com.elanor.customer.dto.CreateAddressRequest;
import com.elanor.customer.service.CustomerService;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.dto.UpdateOrderStatusRequest;
import com.elanor.order.entity.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CouponService couponService;

    private ProductVariant variantA;
    private ProductVariant variantB;
    private String customerToken;
    private UUID customerUserId;

    @BeforeEach
    public void setup() {
        Product p1 = new Product();
        p1.setName("Luxury Serum");
        p1.setSlug("luxury-serum-" + UUID.randomUUID().toString().substring(0, 8));
        p1.setStatus(ProductStatus.ACTIVE);
        p1.setBasePrice(new BigDecimal("999.00"));
        Product savedP1 = productRepository.save(p1);

        variantA = new ProductVariant();
        variantA.setProduct(savedP1);
        variantA.setSku("SERUM-30ML-" + UUID.randomUUID().toString().substring(0, 8));
        variantA.setName("30ml");
        variantA.setPrice(new BigDecimal("999.00"));
        variantA.setActive(true);
        variantA = productVariantRepository.save(variantA);

        StockAdjustmentRequest adjA = new StockAdjustmentRequest();
        adjA.setQuantity(20);
        adjA.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(variantA.getId(), adjA, "TEST");

        Product p2 = new Product();
        p2.setName("Royal Crème");
        p2.setSlug("royal-creme-" + UUID.randomUUID().toString().substring(0, 8));
        p2.setStatus(ProductStatus.ACTIVE);
        p2.setBasePrice(new BigDecimal("1800.00"));
        Product savedP2 = productRepository.save(p2);

        variantB = new ProductVariant();
        variantB.setProduct(savedP2);
        variantB.setSku("CREME-50G-" + UUID.randomUUID().toString().substring(0, 8));
        variantB.setName("50g");
        variantB.setPrice(new BigDecimal("1800.00"));
        variantB.setActive(true);
        variantB = productVariantRepository.save(variantB);

        StockAdjustmentRequest adjB = new StockAdjustmentRequest();
        adjB.setQuantity(10);
        adjB.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(variantB.getId(), adjB, "TEST");

        // Create test user
        String email = "checkout-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Rohan");
        reg.setLastName("Sharma");
        reg.setPhone("9876543210");
        AuthResponse auth = authService.register(reg);
        customerToken = auth.getAccessToken();
        customerUserId = auth.getUserId();
    }

    @Test
    public void testShippingQuote_UnderAndOverThreshold() throws Exception {
        // Below free shipping threshold (e.g. 500 < 1500)
        ShippingQuoteRequest reqBelow = new ShippingQuoteRequest(new BigDecimal("500.00"), "400001", "Maharashtra", "Mumbai");
        mockMvc.perform(post("/api/v1/checkout/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBelow)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shippingCharge").value(99.00))
                .andExpect(jsonPath("$.data.eligibleForFreeShipping").value(false))
                .andExpect(jsonPath("$.data.amountNeededForFreeShipping").value(1000.00))
                .andExpect(jsonPath("$.data.estimatedDeliveryMinDays").value(3))
                .andExpect(jsonPath("$.data.estimatedDeliveryMaxDays").value(7));

        // Over free shipping threshold (e.g. 2000 >= 1500)
        ShippingQuoteRequest reqAbove = new ShippingQuoteRequest(new BigDecimal("2000.00"), "400001", "Maharashtra", "Mumbai");
        mockMvc.perform(post("/api/v1/checkout/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqAbove)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shippingCharge").value(0))
                .andExpect(jsonPath("$.data.eligibleForFreeShipping").value(true))
                .andExpect(jsonPath("$.data.amountNeededForFreeShipping").value(0));
    }

    @Test
    public void testCheckoutValidate_SuccessAndFailureCases() throws Exception {
        String guestToken = UUID.randomUUID().toString();

        // 1. Validate empty cart should fail
        CheckoutValidateRequest emptyReq = new CheckoutValidateRequest();
        emptyReq.setDeliveryAddress(new CheckoutDeliveryAddressDto(null, "Test User", "9876543210", "123 Main St", "Apt 4", "Mumbai", "Maharashtra", "400001", "India"));

        mockMvc.perform(post("/api/v1/checkout/validate")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.errors", hasItem(containsString("cart is empty"))));

        // 2. Add item to cart and validate with address
        AddToCartRequest addReq = new AddToCartRequest(variantA.getId(), 2);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/checkout/validate")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.subtotal").value(1998.00))
                .andExpect(jsonPath("$.data.eligibleForFreeShipping").value(true))
                .andExpect(jsonPath("$.data.shippingCharge").value(0))
                .andExpect(jsonPath("$.data.totalAmount").value(1998.00));
    }

    @Test
    public void testCartCouponApplyAndRemove() throws Exception {
        String couponCode = ("DISCOUNT10-" + UUID.randomUUID().toString().substring(0, 4)).toUpperCase();
        CreateCouponRequest couponReq = new CreateCouponRequest(
                couponCode,
                "10% off",
                DiscountType.PERCENTAGE,
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                null,
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(10, ChronoUnit.DAYS),
                100,
                true
        );
        couponService.createCoupon(couponReq);

        String guestToken = UUID.randomUUID().toString();
        AddToCartRequest addReq = new AddToCartRequest(variantB.getId(), 1); // 1800.00
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk());

        // Apply coupon
        ApplyCartCouponRequest applyReq = new ApplyCartCouponRequest(couponCode);
        mockMvc.perform(post("/api/v1/cart/coupon")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.appliedCouponCode").value(couponCode))
                .andExpect(jsonPath("$.data.discount").value(180.00))
                .andExpect(jsonPath("$.data.total").value(1620.00));

        // Remove coupon
        mockMvc.perform(delete("/api/v1/cart/coupon")
                        .header("X-Guest-Token", guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.appliedCouponCode").doesNotExist())
                .andExpect(jsonPath("$.data.discount").value(0))
                .andExpect(jsonPath("$.data.total").value(1800.00));
    }

    @Test
    public void testAuthenticatedOrderPlacementWithIdempotency() throws Exception {
        // Add saved address for customer
        CreateAddressRequest addrReq = new CreateAddressRequest(
                "Rohan Sharma",
                "9876543210",
                "Penthouse 42, Marine Lines",
                "Opposite Queen's Necklace",
                "Mumbai",
                "Maharashtra",
                "400020",
                "India",
                true
        );
        customerService.createAddress(customerUserId, addrReq);

        // Add items to customer cart
        AddToCartRequest addReq = new AddToCartRequest(variantA.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk());

        // Place Order with Idempotency Key
        String idempotencyKey = UUID.randomUUID().toString();
        CreateOrderRequest orderReq = new CreateOrderRequest(
                null, // will use default customer address
                null,
                null,
                "Please deliver in eco-friendly packaging",
                "DEMO_ONLINE"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.order.orderNumber", startsWith("ELN-")))
                .andExpect(jsonPath("$.data.order.status").value("CREATED"))
                .andExpect(jsonPath("$.data.order.items", hasSize(1)))
                .andExpect(jsonPath("$.data.order.items[0].productName").value("Luxury Serum"))
                .andExpect(jsonPath("$.data.order.address.fullName").value("Rohan Sharma"))
                .andExpect(jsonPath("$.data.paymentRequired").value(true))
                .andReturn();

        // Check that cart is now empty
        mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items", hasSize(0)))
                .andExpect(jsonPath("$.data.totalQuantity").value(0));

        // Resubmit with same Idempotency Key -> Must return same cached order without error
        mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated()) // Idempotent hits return cached 201 response
                .andExpect(jsonPath("$.data.order.orderNumber", startsWith("ELN-")));

        // Verify Customer Orders List
        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].orderNumber", startsWith("ELN-")));
    }
}
