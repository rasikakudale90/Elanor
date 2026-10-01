package com.elanor;

import com.elanor.auth.entity.Role;
import com.elanor.auth.entity.User;
import com.elanor.auth.repository.RoleRepository;
import com.elanor.auth.repository.UserRepository;
import com.elanor.catalog.dto.CreateMediaRequest;
import com.elanor.catalog.dto.CreateProductRequest;
import com.elanor.catalog.dto.CreateVariantRequest;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.category.dto.CreateCategoryRequest;
import com.elanor.category.service.CategoryService;
import com.elanor.common.security.JwtTokenProvider;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    public void setupAdmin() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        User admin = new User();
        admin.setEmail("admin@elanor.com");
        admin.setActive(true);
        admin.getRoles().add(adminRole);
        admin = userRepository.save(admin);

        adminToken = jwtTokenProvider.generateAccessToken(admin.getId(), admin.getEmail(), List.of("ROLE_ADMIN"));
    }

    @Test
    public void shouldManageCategoriesAndHierarchy() throws Exception {
        // Create Parent Category
        CreateCategoryRequest parentReq = new CreateCategoryRequest(null, "Skincare", "skincare", "Premium skincare", null, 1, true);
        mockMvc.perform(post("/api/v1/admin/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Skincare"));

        var categoryTree = categoryService.getCategoryTree();
        UUID parentId = categoryTree.get(0).getId();

        // Create Subcategory
        CreateCategoryRequest subReq = new CreateCategoryRequest(parentId, "Serums", "serums", "Potent facial serums", null, 1, true);
        mockMvc.perform(post("/api/v1/admin/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.parentId").value(parentId.toString()));

        // Check Public Category Tree
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].subcategories[0].name").value("Serums"));
    }

    @Test
    public void shouldCreateProductAndRespectVisibilityRules() throws Exception {
        // 1. Create Active Product
        CreateProductRequest productReq = new CreateProductRequest();
        productReq.setName("Celestial Glow Night Serum");
        productReq.setBasePrice(new BigDecimal("2499.00"));
        productReq.setStatus(ProductStatus.ACTIVE);
        productReq.setShortDescription("Night renewal serum with botanical extracts.");

        mockMvc.perform(post("/api/v1/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.slug").value("celestial-glow-night-serum"));

        // 2. Query Public Catalog
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Celestial Glow Night Serum"));

        // 3. Query Public Product Detail
        mockMvc.perform(get("/api/v1/products/celestial-glow-night-serum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePrice").value(2499.00))
                .andExpect(jsonPath("$.data.variants[0].sku").value("SKU-CELESTIAL-GLOW-NIGHT-SERUM"));
    }

    @Test
    public void shouldHideDraftAndFutureScheduledProductsFromPublic() throws Exception {
        // 1. Create DRAFT Product
        CreateProductRequest draftReq = new CreateProductRequest();
        draftReq.setName("Unreleased Radiant Mask");
        draftReq.setBasePrice(new BigDecimal("1299.00"));
        draftReq.setStatus(ProductStatus.DRAFT);

        mockMvc.perform(post("/api/v1/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftReq)))
                .andExpect(status().isCreated());

        // 2. Create Future SCHEDULED Product
        CreateProductRequest scheduledReq = new CreateProductRequest();
        scheduledReq.setName("Holiday Special Elixir");
        scheduledReq.setBasePrice(new BigDecimal("3999.00"));
        scheduledReq.setStatus(ProductStatus.SCHEDULED);
        scheduledReq.setPublishAt(Instant.now().plus(7, ChronoUnit.DAYS)); // 7 days in future

        mockMvc.perform(post("/api/v1/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduledReq)))
                .andExpect(status().isCreated());

        // 3. Verify public catalog does not return draft or future scheduled products
        mockMvc.perform(get("/api/v1/products/unreleased-radiant-mask"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));

        mockMvc.perform(get("/api/v1/products/holiday-special-elixir"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));
    }
}
