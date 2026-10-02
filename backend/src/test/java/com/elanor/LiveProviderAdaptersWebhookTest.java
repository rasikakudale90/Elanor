package com.elanor;

import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.payment.dto.InitiatePaymentRequest;
import com.elanor.payment.dto.PaymentDto;
import com.elanor.payment.entity.Payment;
import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.entity.PaymentStatus;
import com.elanor.payment.provider.PaymentRequest;
import com.elanor.payment.provider.PaymentVerificationRequest;
import com.elanor.payment.provider.PaymentVerificationResult;
import com.elanor.payment.provider.impl.RazorpayPaymentProvider;
import com.elanor.payment.repository.PaymentRepository;
import com.elanor.payment.service.PaymentService;
import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.enums.ShipmentStatus;
import com.elanor.shipping.repository.ShipmentRepository;
import com.elanor.shipping.service.ShippingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LiveProviderAdaptersWebhookTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RazorpayPaymentProvider razorpayPaymentProvider;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private InventoryService inventoryService;

    private Order testOrder;
    private ProductVariant testVariant;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Cellular Radiance Serum");
        p.setSlug("cellular-radiance-serum-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("3500.00"));
        Product savedP = productRepository.save(p);

        testVariant = new ProductVariant();
        testVariant.setProduct(savedP);
        testVariant.setSku("RAD-30ML-" + UUID.randomUUID().toString().substring(0, 8));
        testVariant.setName("30ml Standard");
        testVariant.setPrice(new BigDecimal("3500.00"));
        testVariant.setActive(true);
        testVariant = productVariantRepository.save(testVariant);

        StockAdjustmentRequest adj = new StockAdjustmentRequest();
        adj.setQuantity(50);
        adj.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(testVariant.getId(), adj, "INIT");

        testOrder = new Order(
                "ELN-WH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                null,
                new BigDecimal("3500.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("3500.00"),
                null,
                3, 5, "Handle with care"
        );
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    public void testRazorpayPaymentProvider_SignatureVerification() {
        String orderId = "order_9A33X56A123456";
        String paymentId = "pay_29QQoUBi66xm2f";
        String secret = "rzp_test_secret_dummy";

        // Generate HMAC SHA256 signature
        String validSignature = computeHmacSha256(orderId + "|" + paymentId, secret);

        PaymentVerificationRequest verifyReq = new PaymentVerificationRequest(
                UUID.randomUUID(),
                orderId,
                paymentId,
                validSignature
        );

        PaymentVerificationResult result = razorpayPaymentProvider.verify(verifyReq);
        assertTrue(result.isSuccessful(), "Razorpay signature verification must pass for valid HMAC signature");

        // Invalid signature test
        PaymentVerificationRequest invalidReq = new PaymentVerificationRequest(
                UUID.randomUUID(),
                orderId,
                paymentId,
                "invalid_signature_hex_123"
        );
        PaymentVerificationResult invalidResult = razorpayPaymentProvider.verify(invalidReq);
        assertFalse(invalidResult.isSuccessful(), "Razorpay verification must fail for invalid signature");
    }

    @Test
    public void testRazorpayWebhook_PaymentCaptured_ConfirmsOrder() throws Exception {
        // 1. Initiate Razorpay payment on the order
        InitiatePaymentRequest initReq = new InitiatePaymentRequest(
                testOrder.getId(),
                PaymentMethod.DEMO_ONLINE,
                PaymentProviderType.RAZORPAY
        );
        PaymentDto paymentDto = paymentService.initiatePayment(initReq, null, true, null);

        String razorpayOrderId = paymentDto.getTransactionRef();
        String razorpayPaymentId = "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        // 2. Simulate Razorpay webhook payload
        String webhookPayload = "{\n" +
                "  \"entity\": \"event\",\n" +
                "  \"event\": \"payment.captured\",\n" +
                "  \"payload\": {\n" +
                "    \"payment\": {\n" +
                "      \"entity\": {\n" +
                "        \"id\": \"" + razorpayPaymentId + "\",\n" +
                "        \"order_id\": \"" + razorpayOrderId + "\",\n" +
                "        \"amount\": 350000,\n" +
                "        \"currency\": \"INR\",\n" +
                "        \"status\": \"captured\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/api/v1/webhooks/razorpay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.event").value("payment.captured"));

        // 3. Verify Payment and Order status in DB
        Payment updatedPayment = paymentRepository.findByTransactionRef(razorpayOrderId).orElseThrow();
        assertEquals(PaymentStatus.SUCCESSFUL, updatedPayment.getStatus());

        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
    }

    @Test
    public void testShiprocketWebhook_MilestoneProgression_Delivered() throws Exception {
        // 1. Create a shipment for testOrder
        String trackingNumber = "SR-TRACK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Shipment shipment = new Shipment();
        shipment.setOrder(testOrder);
        shipment.setCarrierName("Shiprocket / Delhivery");
        shipment.setTrackingNumber(trackingNumber);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment = shipmentRepository.save(shipment);

        // 2. Dispatch Shiprocket webhook
        Map<String, Object> payload = Map.of(
                "awb", trackingNumber,
                "current_status", "DELIVERED",
                "location", "Mumbai Delivery Hub",
                "activity", "Package delivered and signed by customer"
        );

        mockMvc.perform(post("/api/v1/webhooks/shiprocket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. Verify shipment and order transition to DELIVERED
        Shipment updatedShipment = shipmentRepository.findByTrackingNumber(trackingNumber).orElseThrow();
        assertEquals(ShipmentStatus.DELIVERED, updatedShipment.getStatus());
        assertNotNull(updatedShipment.getDeliveredAt());

        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.DELIVERED, updatedOrder.getStatus());
    }

    private String computeHmacSha256(String data, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
