package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.cart.dto.AddToCartRequest;
import com.elanor.cart.dto.MergeCartRequest;
import com.elanor.cart.dto.UpdateCartItemRequest;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.inventory.dto.StockAdjustmentRequest;
import com.elanor.inventory.entity.MovementType;
import com.elanor.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerTest {

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

    private ProductVariant variantA;
    private ProductVariant variantB;

    @BeforeEach
    public void setupCatalogAndStock() {
        Product productA = new Product();
        productA.setName("Pure Rose Toner");
        productA.setSlug("pure-rose-toner-" + UUID.randomUUID());
        productA.setBasePrice(new BigDecimal("799.00"));
        productA.setStatus(ProductStatus.ACTIVE);
        Product savedA = productRepository.save(productA);

        variantA = new ProductVariant(savedA, "SKU-TONER-" + UUID.randomUUID().toString().substring(0, 8), "150ml", new BigDecimal("799.00"), null, "{}");
        variantA = productVariantRepository.save(variantA);
        inventoryService.adjustStock(variantA.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 20, "Restock A", "PO-A"), "Admin");

        Product productB = new Product();
        productB.setName("Luminous Eye Serum");
        productB.setSlug("luminous-eye-serum-" + UUID.randomUUID());
        productB.setBasePrice(new BigDecimal("1299.00"));
        productB.setStatus(ProductStatus.ACTIVE);
        Product savedB = productRepository.save(productB);

        variantB = new ProductVariant(savedB, "SKU-EYE-" + UUID.randomUUID().toString().substring(0, 8), "15ml", new BigDecimal("1299.00"), null, "{}");
        variantB = productVariantRepository.save(variantB);
        inventoryService.adjustStock(variantB.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 5, "Restock B", "PO-B"), "Admin");
    }

    @Test
    public void shouldAddUpdateAndCalculateServerAuthoritativeTotals() throws Exception {
        String guestToken = "guest-cart-" + UUID.randomUUID();

        // 1. Add 1 item (subtotal = 799.00, below free threshold of 1500.00 -> shipping charge = 99.00, total = 898.00)
        AddToCartRequest addReq = new AddToCartRequest(variantA.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subtotal").value(799.00))
                .andExpect(jsonPath("$.data.shippingCharge").value(99.00))
                .andExpect(jsonPath("$.data.total").value(898.00))
                .andExpect(jsonPath("$.data.eligibleForFreeShipping").value(false));

        // 2. Add another item (variantB = 1299.00 -> subtotal = 799 + 1299 = 2098.00 >= 1500.00 -> free shipping!)
        AddToCartRequest addReq2 = new AddToCartRequest(variantB.getId(), 1);
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subtotal").value(2098.00))
                .andExpect(jsonPath("$.data.shippingCharge").value(0.00))
                .andExpect(jsonPath("$.data.total").value(2098.00))
                .andExpect(jsonPath("$.data.eligibleForFreeShipping").value(true));

        // 3. Update quantity of variantA to 2 (subtotal = 799*2 + 1299 = 2897.00)
        UpdateCartItemRequest updateReq = new UpdateCartItemRequest(2);
        mockMvc.perform(patch("/api/v1/cart/items/" + variantA.getId())
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalQuantity").value(3))
                .andExpect(jsonPath("$.data.subtotal").value(2897.00));
    }

    @Test
    public void shouldExecuteCriticalCartMergeTestCaseFromSRS() throws Exception {
        // SRS §45:
        // Guest has SKU-A qty 2
        // Customer has SKU-A qty 1
        // Stock = 5 (set available stock to 5)
        // Expected: Customer cart has SKU-A qty 3
        Product productMerge = new Product();
        productMerge.setName("Test Merge Serum");
        productMerge.setSlug("test-merge-serum-" + UUID.randomUUID());
        productMerge.setBasePrice(new BigDecimal("1000.00"));
        productMerge.setStatus(ProductStatus.ACTIVE);
        Product savedM = productRepository.save(productMerge);

        ProductVariant variantM = new ProductVariant(savedM, "SKU-MERGE-" + UUID.randomUUID().toString().substring(0, 8), "30ml", new BigDecimal("1000.00"), null, "{}");
        variantM = productVariantRepository.save(variantM);
        inventoryService.adjustStock(variantM.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 5, "Restock 5", "PO-M"), "Admin");

        String guestToken = "guest-merge-" + UUID.randomUUID();

        // 1. Guest adds 2 units
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(variantM.getId(), 2))))
                .andExpect(status().isOk());

        // 2. Customer registers and adds 1 unit
        RegisterRequest register = new RegisterRequest("Damon", "Salvatore", "damon.cart@example.com", "+919988776611", "Password123!");
        AuthResponse auth = authService.register(register);

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(variantM.getId(), 1))))
                .andExpect(status().isOk());

        // 3. Perform Merge on login
        mockMvc.perform(post("/api/v1/cart/merge")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MergeCartRequest(guestToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(3)) // 2 + 1 = 3
                .andExpect(jsonPath("$.data.subtotal").value(3000.00));
    }

    @Test
    public void shouldCapMergedQuantityToAvailableStockWithoutOverselling() throws Exception {
        // SRS §45:
        // Guest qty 4, Customer qty 3, Stock = 5
        // Expected: Final merged qty capped to 5 (no overselling)
        Product productCap = new Product();
        productCap.setName("Test Cap Serum");
        productCap.setSlug("test-cap-serum-" + UUID.randomUUID());
        productCap.setBasePrice(new BigDecimal("500.00"));
        productCap.setStatus(ProductStatus.ACTIVE);
        Product savedC = productRepository.save(productCap);

        ProductVariant variantC = new ProductVariant(savedC, "SKU-CAP-" + UUID.randomUUID().toString().substring(0, 8), "30ml", new BigDecimal("500.00"), null, "{}");
        variantC = productVariantRepository.save(variantC);
        inventoryService.adjustStock(variantC.getId(), new StockAdjustmentRequest(MovementType.RESTOCK, 5, "Restock 5", "PO-C"), "Admin");

        String guestToken = "guest-cap-" + UUID.randomUUID();

        // Guest adds 4 units
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(variantC.getId(), 4))))
                .andExpect(status().isOk());

        // Customer registers and adds 3 units (temporarily restock +2 so customer can add 3, total onHand is 5)
        RegisterRequest register = new RegisterRequest("Klaus", "Mikaelson", "klaus.cart@example.com", "+919988776622", "Password123!");
        AuthResponse auth = authService.register(register);

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(variantC.getId(), 3))))
                .andExpect(status().isOk());

        // Perform merge -> total requested is 4+3=7, but stock is 5 -> capped at 5
        mockMvc.perform(post("/api/v1/cart/merge")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MergeCartRequest(guestToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(5))
                .andExpect(jsonPath("$.data.subtotal").value(2500.00));
    }
}
