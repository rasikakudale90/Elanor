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
import com.elanor.order.entity.OrderStatus;
import com.elanor.shipping.dto.AddShipmentEventRequest;
import com.elanor.shipping.dto.CreateShipmentRequest;
import com.elanor.shipping.enums.ShipmentProviderType;
import com.elanor.shipping.enums.ShipmentStatus;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ShippingControllerTest {

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
    private JwtTokenProvider jwtTokenProvider;

    private ProductVariant variant;
    private String customerToken;
    private String adminToken;
    private UUID customerUserId;

    @BeforeEach
    public void setup() {
        // Setup Catalog & Stock
        Product p = new Product();
        p.setName("Botanical Cellular Essence");
        p.setSlug("botanical-cellular-essence-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("1850.00"));
        Product savedP = productRepository.save(p);

        variant = new ProductVariant();
        variant.setProduct(savedP);
        variant.setSku("ESSENCE-100ML-" + UUID.randomUUID().toString().substring(0, 8));
        variant.setName("100ml");
        variant.setPrice(new BigDecimal("1850.00"));
        variant.setActive(true);
        variant = productVariantRepository.save(variant);

        StockAdjustmentRequest adj = new StockAdjustmentRequest();
        adj.setQuantity(30);
        adj.setMovementType(MovementType.RESTOCK);
        inventoryService.adjustStock(variant.getId(), adj, "TEST");

        // Setup Customer
        String email = "shipping-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Elena");
        reg.setLastName("Rostova");
        reg.setPhone("9876543210");
        AuthResponse authRes = authService.register(reg);
        customerToken = authRes.getAccessToken();
        customerUserId = authRes.getUserId();

        CreateAddressRequest addrReq = new CreateAddressRequest(
                "Elena Rostova", "9876543210", "42 Rue Botanique", "Suite 101",
                "Mumbai", "Maharashtra", "400001", "India", true
        );
        customerService.createAddress(customerUserId, addrReq);

        // Setup Admin
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        User adminUser = new User();
        adminUser.setEmail("admin-shipping-" + UUID.randomUUID().toString().substring(0, 8) + "@elanor.com");
        adminUser.setPasswordHash("hash");
        adminUser.setEmailVerified(true);
        adminUser.getRoles().add(adminRole);
        adminUser = userRepository.save(adminUser);

        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getId(), adminUser.getEmail(), List.of("ROLE_ADMIN"));
    }

    private UUID createTestOrder() throws Exception {
        // Add item to cart
        AddToCartRequest cartReq = new AddToCartRequest(variant.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartReq)))
                .andExpect(status().isOk());

        CreateOrderRequest orderReq = new CreateOrderRequest(
                null,
                null,
                null,
                "Please handle with care",
                "DEMO_ONLINE"
        );

        MvcResult orderResult = mockMvc.perform(post("/api/v1/checkout/order")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        return UUID.fromString(json.path("data").path("order").path("id").asText());
    }

    @Test
    public void testFullShippingLifecycle() throws Exception {
        UUID orderId = createTestOrder();
        String trackingNum = "ELN-TRACK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 1. Admin creates shipment
        CreateShipmentRequest shipReq = new CreateShipmentRequest(
                orderId,
                ShipmentProviderType.MANUAL,
                "BlueDart Express",
                trackingNum,
                "https://bluedart.com/track/" + trackingNum,
                ShipmentStatus.IN_TRANSIT,
                "Mumbai Hub",
                "Dispatched from main distribution warehouse"
        );

        MvcResult shipResult = mockMvc.perform(post("/api/v1/admin/shipments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shipReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trackingNumber").value(trackingNum))
                .andExpect(jsonPath("$.data.status").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.data.events", hasSize(1)))
                .andReturn();

        JsonNode shipNode = objectMapper.readTree(shipResult.getResponse().getContentAsString());
        UUID shipmentId = UUID.fromString(shipNode.path("data").path("id").asText());

        // Verify Order status is updated to SHIPPED
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        // 2. Public Tracking endpoint
        mockMvc.perform(get("/api/v1/shipments/track/" + trackingNum))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trackingNumber").value(trackingNum))
                .andExpect(jsonPath("$.data.carrierName").value("BlueDart Express"))
                .andExpect(jsonPath("$.data.status").value("IN_TRANSIT"));

        // 3. Customer order shipments endpoint
        mockMvc.perform(get("/api/v1/shipments/order/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].trackingNumber").value(trackingNum));

        // 4. Admin adds OUT_FOR_DELIVERY event
        AddShipmentEventRequest outForDeliveryEvent = new AddShipmentEventRequest(
                "OUT_FOR_DELIVERY", "Bandra Local Hub", "Courier assigned for final delivery", Instant.now()
        );
        mockMvc.perform(post("/api/v1/admin/shipments/" + shipmentId + "/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(outForDeliveryEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OUT_FOR_DELIVERY"))
                .andExpect(jsonPath("$.data.events", hasSize(2)));

        // Verify Order status is now OUT_FOR_DELIVERY
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OUT_FOR_DELIVERY"));

        // 5. Admin adds DELIVERED event
        AddShipmentEventRequest deliveredEvent = new AddShipmentEventRequest(
                "DELIVERED", "Customer Residence", "Delivered and signed by recipient", Instant.now()
        );
        mockMvc.perform(post("/api/v1/admin/shipments/" + shipmentId + "/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveredEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"))
                .andExpect(jsonPath("$.data.deliveredAt", notNullValue()))
                .andExpect(jsonPath("$.data.events", hasSize(3)));

        // Verify Order status is now DELIVERED
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    @Test
    public void testCustomerCannotAccessAdminShippingEndpoints() throws Exception {
        UUID orderId = createTestOrder();
        CreateShipmentRequest shipReq = new CreateShipmentRequest(
                orderId, ShipmentProviderType.MANUAL, "BlueDart", "TRACK-999", null, ShipmentStatus.IN_TRANSIT, "Hub", "Started"
        );

        mockMvc.perform(post("/api/v1/admin/shipments")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shipReq)))
                .andExpect(status().isForbidden());
    }
}
