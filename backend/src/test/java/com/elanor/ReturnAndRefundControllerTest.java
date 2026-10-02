package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.entity.Role;
import com.elanor.auth.entity.User;
import com.elanor.auth.repository.RoleRepository;
import com.elanor.auth.repository.UserRepository;
import com.elanor.auth.service.AuthService;
import com.elanor.cart.dto.AddToCartRequest;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.checkout.dto.CreateOrderRequest;
import com.elanor.common.security.JwtTokenProvider;
import com.elanor.customer.dto.CreateAddressRequest;
import com.elanor.customer.service.CustomerService;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.elanor.order.entity.Order;
import com.elanor.order.entity.OrderStatus;
import com.elanor.order.repository.OrderRepository;
import com.elanor.refund.dto.ProcessRefundRequest;
import com.elanor.refund.enums.RefundMethod;
import com.elanor.returns.dto.CreateReturnItemDto;
import com.elanor.returns.dto.CreateReturnRequestDto;
import com.elanor.returns.dto.UpdateReturnStatusRequest;
import com.elanor.returns.enums.ReturnStatus;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReturnAndRefundControllerTest {

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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private ProductVariant variant;
    private String customerToken;
    private String adminToken;
    private UUID customerUserId;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Cellular Replenishing Cream");
        p.setSlug("cellular-replenishing-cream-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("2100.00"));
        Product savedP = productRepository.save(p);

        variant = new ProductVariant();
        variant.setProduct(savedP);
        variant.setSku("CREAM-50ML-" + UUID.randomUUID().toString().substring(0, 8));
        variant.setName("50ml Jar");
        variant.setPrice(new BigDecimal("2100.00"));
        variant.setActive(true);
        variant = productVariantRepository.save(variant);

        StockAdjustmentRequest adj = new StockAdjustmentRequest();
        adj.setQuantity(40);
        adj.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(variant.getId(), adj, "TEST");

        // Setup Customer
        String email = "returns-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Sophie");
        reg.setLastName("Laurent");
        reg.setPhone("9123456780");
        AuthResponse authRes = authService.register(reg);
        customerToken = authRes.getAccessToken();
        customerUserId = authRes.getUserId();

        CreateAddressRequest addrReq = new CreateAddressRequest(
                "Sophie Laurent", "9123456780", "15 Boulevard Saint-Germain", "",
                "Pune", "Maharashtra", "411001", "India", true
        );
        customerService.createAddress(customerUserId, addrReq);

        // Setup Admin
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        User adminUser = new User();
        adminUser.setEmail("admin-returns-" + UUID.randomUUID().toString().substring(0, 8) + "@elanor.com");
        adminUser.setPasswordHash("hash");
        adminUser.setEmailVerified(true);
        adminUser.getRoles().add(adminRole);
        adminUser = userRepository.save(adminUser);

        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getId(), adminUser.getEmail(), List.of("ROLE_ADMIN"));
    }

    private Order createDeliveredOrder() throws Exception {
        AddToCartRequest cartReq = new AddToCartRequest(variant.getId(), 2);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartReq)))
                .andExpect(status().isOk());

        CreateOrderRequest orderReq = new CreateOrderRequest(
                null, null, null, "Gift wrap", "DEMO_ONLINE"
        );

        MvcResult orderResult = mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        UUID orderId = UUID.fromString(json.path("data").path("order").path("id").asText());

        // Update status to DELIVERED
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Test
    public void testReturnRejectionWhenNotDelivered() throws Exception {
        AddToCartRequest cartReq = new AddToCartRequest(variant.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartReq)))
                .andExpect(status().isOk());

        CreateOrderRequest orderReq = new CreateOrderRequest(null, null, null, "Test", "DEMO_ONLINE");
        MvcResult orderResult = mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        UUID orderId = UUID.fromString(json.path("data").path("order").path("id").asText());
        UUID orderItemId = UUID.fromString(json.path("data").path("order").path("items").get(0).path("id").asText());

        // Order is in CREATED status, return should be rejected
        CreateReturnRequestDto returnReq = new CreateReturnRequestDto(
                orderId, "Damaged in transit", "Jar seal broken", false,
                List.of(new CreateReturnItemDto(orderItemId, 1, "Unopened with seal broken"))
        );

        mockMvc.perform(post("/api/v1/returns")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("RETURN_NOT_ELIGIBLE"));
    }

    @Test
    public void testFullReturnAndRestockLifecycle() throws Exception {
        Order order = createDeliveredOrder();
        UUID orderId = order.getId();
        UUID orderItemId = order.getItems().get(0).getId();

        int stockBeforeReturn = inventoryService.getInventoryByVariantId(variant.getId()).getOnHand();

        // 1. Customer submits return request
        CreateReturnRequestDto returnReq = new CreateReturnRequestDto(
                orderId, "Skin reaction / Sensitivity", "Mild redness after first test patch", false,
                List.of(new CreateReturnItemDto(orderItemId, 1, "Opened but 95% full"))
        );

        MvcResult returnRes = mockMvc.perform(post("/api/v1/returns")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REQUESTED"))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andReturn();

        JsonNode returnNode = objectMapper.readTree(returnRes.getResponse().getContentAsString());
        UUID returnId = UUID.fromString(returnNode.path("data").path("id").asText());

        // 2. Duplicate return submission prevention
        mockMvc.perform(post("/api/v1/returns")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("RETURN_ALREADY_REQUESTED"));

        // 3. Customer lists own returns
        mockMvc.perform(get("/api/v1/returns/my")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(returnId.toString()));

        // 4. Admin approves return and sets to RECEIVED with restock
        UpdateReturnStatusRequest updateReq = new UpdateReturnStatusRequest(ReturnStatus.RECEIVED, "Item inspected and validated", true);
        mockMvc.perform(put("/api/v1/admin/returns/" + returnId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RECEIVED"));

        // Verify stock has been incremented by 1
        int stockAfterRestock = inventoryService.getInventoryByVariantId(variant.getId()).getOnHand();
        assertEquals(stockBeforeReturn + 1, stockAfterRestock);
    }

    @Test
    public void testRefundProcessingAndValidation() throws Exception {
        Order order = createDeliveredOrder();
        UUID orderId = order.getId();
        BigDecimal totalAmount = order.getTotalAmount();

        // 1. Attempt refund exceeding total amount -> Expect rejection
        ProcessRefundRequest excessiveRefund = new ProcessRefundRequest(
                orderId, null, totalAmount.add(BigDecimal.valueOf(500)),
                RefundMethod.ORIGINAL_SOURCE, "REF-EXCESS", "Admin"
        );
        mockMvc.perform(post("/api/v1/admin/refunds")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessiveRefund)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("REFUND_AMOUNT_EXCEEDED"));

        // 2. Issue valid full refund
        ProcessRefundRequest validRefund = new ProcessRefundRequest(
                orderId, null, totalAmount,
                RefundMethod.ORIGINAL_SOURCE, "REF-FULL-100", "Admin"
        );
        mockMvc.perform(post("/api/v1/admin/refunds")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRefund)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REFUND_COMPLETED"))
                .andExpect(jsonPath("$.data.amount").value(totalAmount.doubleValue()));

        // Verify order status is updated to REFUNDED
        Order refundedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.REFUNDED, refundedOrder.getStatus());
    }
}
