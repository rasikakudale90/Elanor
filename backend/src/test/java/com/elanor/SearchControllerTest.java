package com.elanor;

import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.category.entity.Category;
import com.elanor.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        productVariantRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category serums = new Category();
        serums.setName("Serums");
        serums.setSlug("serums");
        serums.setDescription("High potency botanical elixirs");
        serums.setDisplayOrder(1);
        serums.setActive(true);
        serums = categoryRepository.save(serums);

        Category creams = new Category();
        creams.setName("Creams");
        creams.setSlug("creams");
        creams.setDescription("Deeply nourishing hydration creams");
        creams.setDisplayOrder(2);
        creams.setActive(true);
        creams = categoryRepository.save(creams);

        // Product 1: Golden Radiance Serum
        Product p1 = new Product();
        p1.setCategory(serums);
        p1.setName("Golden Radiance Vitamin C Serum");
        p1.setSlug("golden-radiance-serum-" + UUID.randomUUID());
        p1.setShortDescription("Illuminating Vitamin C formula");
        p1.setDescription("Concentrated antioxidant serum infused with 24k gold flakes.");
        p1.setBasePrice(BigDecimal.valueOf(2400.00));
        p1.setStatus(ProductStatus.ACTIVE);
        p1.setMetaTitle("Radiance Serum");
        p1.setMetaDescription("Luxury Vitamin C");
        p1.setBadges("BESTSELLER,ORGANIC");
        p1 = productRepository.save(p1);

        ProductVariant v1 = new ProductVariant(p1, "GRS-30ML-" + UUID.randomUUID(), "30ml Bottle",
                BigDecimal.valueOf(2400.00), BigDecimal.valueOf(2800.00), "{\"volume\":\"30ml\"}");
        productVariantRepository.save(v1);

        // Product 2: Velvet Hydration Night Cream
        Product p2 = new Product();
        p2.setCategory(creams);
        p2.setName("Velvet Hydration Night Cream");
        p2.setSlug("velvet-hydration-cream-" + UUID.randomUUID());
        p2.setShortDescription("Restorative bedtime moisture");
        p2.setDescription("Nourishes skin barrier overnight with peptide rich ceramides.");
        p2.setBasePrice(BigDecimal.valueOf(1800.00));
        p2.setStatus(ProductStatus.ACTIVE);
        p2.setMetaTitle("Night Cream");
        p2.setMetaDescription("Velvet Hydration");
        p2.setBadges("NEW,VEGAN");
        p2 = productRepository.save(p2);

        ProductVariant v2 = new ProductVariant(p2, "VHC-50ML-" + UUID.randomUUID(), "50ml Jar",
                BigDecimal.valueOf(1800.00), BigDecimal.valueOf(2100.00), "{\"volume\":\"50ml\"}");
        productVariantRepository.save(v2);
    }

    @Test
    @DisplayName("Search by keyword 'Vitamin C' finds Golden Radiance Serum")
    void testSearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "Vitamin C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Golden Radiance Vitamin C Serum"));
    }

    @Test
    @DisplayName("Search by category 'creams' finds Velvet Hydration Night Cream")
    void testSearchByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("category", "creams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Velvet Hydration Night Cream"));
    }

    @Test
    @DisplayName("Search with price filter 2000 to 3000 returns only serum")
    void testSearchByPriceRange() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("minPrice", "2000")
                        .param("maxPrice", "3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Golden Radiance Vitamin C Serum"));
    }

    @Test
    @DisplayName("Search by badge 'BESTSELLER' returns Golden Radiance Serum")
    void testSearchByBadge() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("badge", "BESTSELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Golden Radiance Vitamin C Serum"));
    }

    @Test
    @DisplayName("Get instant search suggestions for 'serum' returns matching titles and categories")
    void testSearchSuggestions() throws Exception {
        mockMvc.perform(get("/api/v1/search/suggestions")
                        .param("q", "serum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.suggestions.length()").value(2))
                .andExpect(jsonPath("$.data.products.length()").value(1))
                .andExpect(jsonPath("$.data.categories.length()").value(1));
    }
}
