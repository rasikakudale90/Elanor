package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.service.AuthService;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.wishlist.dto.AddWishlistItemRequest;
import com.elanor.wishlist.dto.MergeWishlistRequest;
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
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private AuthService authService;

    private ProductVariant variant1;
    private ProductVariant variant2;

    @BeforeEach
    public void setupProducts() {
        Product product1 = new Product();
        product1.setName("Golden Elixir Facial Oil");
        product1.setSlug("golden-elixir-facial-oil-" + UUID.randomUUID());
        product1.setBasePrice(new BigDecimal("1999.00"));
        product1.setStatus(ProductStatus.ACTIVE);
        Product saved1 = productRepository.save(product1);

        variant1 = new ProductVariant(saved1, "SKU-OIL-" + UUID.randomUUID().toString().substring(0, 8), "30ml", new BigDecimal("1999.00"), null, "{}");
        variant1 = productVariantRepository.save(variant1);

        Product product2 = new Product();
        product2.setName("Velvet Hydration Cream");
        product2.setSlug("velvet-hydration-cream-" + UUID.randomUUID());
        product2.setBasePrice(new BigDecimal("1499.00"));
        product2.setStatus(ProductStatus.ACTIVE);
        Product saved2 = productRepository.save(product2);

        variant2 = new ProductVariant(saved2, "SKU-CREAM-" + UUID.randomUUID().toString().substring(0, 8), "50ml", new BigDecimal("1499.00"), null, "{}");
        variant2 = productVariantRepository.save(variant2);
    }

    @Test
    public void shouldManageGuestWishlistAndMergeIntoCustomerAccount() throws Exception {
        String guestToken = "guest-token-" + UUID.randomUUID();

        // 1. Add item to guest wishlist
        AddWishlistItemRequest addReq1 = new AddWishlistItemRequest(variant1.getId());
        mockMvc.perform(post("/api/v1/wishlist/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(1))
                .andExpect(jsonPath("$.data.items[0].sku").value(variant1.getSku()));

        // 2. Attempt duplicate add (should deduplicate)
        mockMvc.perform(post("/api/v1/wishlist/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(1));

        // 3. Register a customer and add variant2 to customer wishlist
        RegisterRequest register = new RegisterRequest("Elena", "Gilbert", "elena.wishlist@example.com", "+919988112233", "Password123!");
        AuthResponse auth = authService.register(register);

        AddWishlistItemRequest addReq2 = new AddWishlistItemRequest(variant2.getId());
        mockMvc.perform(post("/api/v1/wishlist/items")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(1));

        // 4. Merge guest wishlist into customer wishlist
        MergeWishlistRequest mergeReq = new MergeWishlistRequest(guestToken);
        mockMvc.perform(post("/api/v1/wishlist/merge")
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(2));
    }
}
