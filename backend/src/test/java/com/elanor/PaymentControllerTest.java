package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.cart.dto.AddToCartRequest;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.checkout.dto.CheckoutDeliveryAddressDto;
import com.elanor.checkout.dto.CreateOrderRequest;
import com.elanor.customer.dto.CreateAddressRequest;
import com.elanor.customer.service.CustomerService;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.dto.CancelOrderRequest;
import com.elanor.order.dto.UpdateOrderStatusRequest;
import com.elanor.order.entity.OrderStatus;
import com.elanor.payment.dto.DemoPaymentResultRequest;
import com.elanor.payment.dto.InitiatePaymentRequest;
import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PaymentControllerTest {

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

    private ProductVariant variant;
    private String customerToken;
    private UUID customerUserId;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Rose Radiance Elixir");
        p.setSlug("rose-radiance-elixir-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("1200.00"));
        Product savedP = productRepository.save(p);

        variant = new ProductVariant();
        variant.setProduct(savedP);
        variant.setSku("ROSE-50ML-" + UUID.randomUUID().toString().substring(0, 8));
        variant.setName("50ml");
        variant.setPrice(new BigDecimal("1200.00"));
        variant.setActive(true);
        variant = productVariantRepository.save(variant);

        StockAdjustmentRequest adj = new StockAdjustmentRequest();
        adj.setQuantity(25);
        adj.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(variant.getId(), adj, "TEST");

        String email = "payment-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Ananya");
        reg.setLastName("Deshmukh");
        reg.setPhone("9988776655");
        AuthResponse auth = authService.register(reg);
        customerToken = auth.getAccessToken();
        customerUserId = auth.getUserId();

        CreateAddressRequest addrReq = new CreateAddressRequest(
                "Ananya Deshmukh",
                "9988776655",
                "Villa 7, Palm Grove",
                "Near Juhu Beach",
                "Mumbai",
                "Maharashtra",
                "400049",
                "India",
                true
        );
        customerService.createAddress(customerUserId, addrReq);
    }

    private UUID createTestOrder() throws Exception {
        AddToCartRequest addReq = new AddToCartRequest(variant.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk());

        CreateOrderRequest orderReq = new CreateOrderRequest(
                null,
                null,
                null,
                "Fragile handle with care",
                "DEMO_ONLINE"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(root.path("data").path("order").path("id").asText());
    }

    @Test
    public void testDemoPaymentFlow_SuccessOutcome() throws Exception {
        UUID orderId = createTestOrder();

        // 1. Initiate Demo Payment
        String idempotencyKey = UUID.randomUUID().toString();
        InitiatePaymentRequest initReq = new InitiatePaymentRequest(orderId, PaymentMethod.DEMO_ONLINE, PaymentProviderType.DEMO);

        MvcResult initResult = mockMvc.perform(post("/api/v1/payments/initiate")
                        .header("Authorization", "Bearer " + customerToken)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentProvider").value("DEMO"))
                .andExpect(jsonPath("$.data.paymentMethod").value("DEMO_ONLINE"))
                .andExpect(jsonPath("$.data.status").value("INITIATED"))
                .andExpect(jsonPath("$.data.transactionRef", startsWith("DEMO-TXN-")))
                .andReturn();

        JsonNode initNode = objectMapper.readTree(initResult.getResponse().getContentAsString());
        UUID paymentId = UUID.fromString(initNode.path("data").path("id").asText());

        // 2. Process Demo Result - SUCCESS
        DemoPaymentResultRequest resultReq = new DemoPaymentResultRequest(true);
        mockMvc.perform(post("/api/v1/payments/" + paymentId + "/demo-result")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESSFUL"));

        // 3. Verify Order is now CONFIRMED
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // 4. Verify Duplicate payment initiation is rejected
        mockMvc.perform(post("/api/v1/payments/initiate")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initReq)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testDemoPaymentFlow_FailureOutcome() throws Exception {
        UUID orderId = createTestOrder();

        InitiatePaymentRequest initReq = new InitiatePaymentRequest(orderId, PaymentMethod.DEMO_ONLINE, PaymentProviderType.DEMO);
        MvcResult initResult = mockMvc.perform(post("/api/v1/payments/initiate")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode initNode = objectMapper.readTree(initResult.getResponse().getContentAsString());
        UUID paymentId = UUID.fromString(initNode.path("data").path("id").asText());

        // Process Demo Result - FAILURE
        DemoPaymentResultRequest resultReq = new DemoPaymentResultRequest(false);
        mockMvc.perform(post("/api/v1/payments/" + paymentId + "/demo-result")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("FAILED"))
                .andExpect(jsonPath("$.data.errorMessage", notNullValue()));

        // Order should remain CREATED
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    public void testCodPaymentLifecycle() throws Exception {
        UUID orderId = createTestOrder();

        // 1. Initiate COD Payment
        InitiatePaymentRequest initReq = new InitiatePaymentRequest(orderId, PaymentMethod.COD, PaymentProviderType.COD);
        MvcResult initResult = mockMvc.perform(post("/api/v1/payments/initiate")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.paymentProvider").value("COD"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn();

        JsonNode initNode = objectMapper.readTree(initResult.getResponse().getContentAsString());
        UUID paymentId = UUID.fromString(initNode.path("data").path("id").asText());

        // COD automatically moves order to CONFIRMED
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // 2. Admin confirms collection (mocking admin role)
        // With test context or direct service call, verify payment lookup
        mockMvc.perform(get("/api/v1/payments/" + paymentId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    public void testOrderCancellationBeforeShipping() throws Exception {
        UUID orderId = createTestOrder();

        // Cancel order
        CancelOrderRequest cancelReq = new CancelOrderRequest("Changed my mind, ordered wrong size");
        mockMvc.perform(post("/api/v1/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.statusHistory", hasSize(2)));

        // Attempting to cancel again should fail
        mockMvc.perform(post("/api/v1/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelReq)))
                .andExpect(status().isBadRequest());
    }
}
