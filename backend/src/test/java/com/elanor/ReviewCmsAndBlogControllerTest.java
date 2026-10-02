package com.elanor;

import com.elanor.auth.dto.AuthResponse;
import com.elanor.auth.dto.RegisterRequest;
import com.elanor.auth.entity.Role;
import com.elanor.auth.entity.User;
import com.elanor.auth.repository.RoleRepository;
import com.elanor.auth.repository.UserRepository;
import com.elanor.auth.service.AuthService;
import com.elanor.blog.dto.CreateBlogPostRequest;
import com.elanor.blog.enums.BlogPostStatus;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.cms.dto.CreateBannerRequest;
import com.elanor.common.security.JwtTokenProvider;
import com.elanor.review.dto.CreateReviewRequest;
import com.elanor.review.dto.ModerateReviewRequest;
import com.elanor.review.enums.ReviewStatus;
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
public class ReviewCmsAndBlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Product product;
    private String customerToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Cellular Youth Serum");
        p.setSlug("cellular-youth-serum-" + UUID.randomUUID().toString().substring(0, 8));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("3200.00"));
        product = productRepository.save(p);

        // Customer Setup
        String email = "review-test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setFirstName("Genevieve");
        reg.setLastName("Moreau");
        reg.setPhone("9811223344");
        AuthResponse authRes = authService.register(reg);
        customerToken = authRes.getAccessToken();

        // Admin Setup
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        User adminUser = new User();
        adminUser.setEmail("admin-cms-" + UUID.randomUUID().toString().substring(0, 8) + "@elanor.com");
        adminUser.setPasswordHash("hash");
        adminUser.setEmailVerified(true);
        adminUser.getRoles().add(adminRole);
        adminUser = userRepository.save(adminUser);

        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getId(), adminUser.getEmail(), List.of("ROLE_ADMIN"));
    }

    @Test
    public void testReviewSubmissionAndModerationFlow() throws Exception {
        // 1. Submit review
        CreateReviewRequest reviewReq = new CreateReviewRequest(
                "Genevieve Moreau",
                5,
                "Sublime texture and hydration",
                "Within 3 days my skin felt completely restored and radiant.",
                List.of("https://cdn.elanor.com/reviews/photo1.jpg")
        );

        MvcResult submitRes = mockMvc.perform(post("/api/v1/products/" + product.getId() + "/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn();

        JsonNode submitNode = objectMapper.readTree(submitRes.getResponse().getContentAsString());
        UUID reviewId = UUID.fromString(submitNode.path("data").path("id").asText());

        // 2. Public product reviews should NOT show pending review
        mockMvc.perform(get("/api/v1/products/" + product.getId() + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));

        // 3. Admin approves review
        ModerateReviewRequest modReq = new ModerateReviewRequest(ReviewStatus.APPROVED);
        mockMvc.perform(put("/api/v1/admin/reviews/" + reviewId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // 4. Public product reviews now includes approved review
        mockMvc.perform(get("/api/v1/products/" + product.getId() + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].rating").value(5))
                .andExpect(jsonPath("$.data.content[0].customerName").value("Genevieve Moreau"));
    }

    @Test
    public void testCmsBannerLifecycle() throws Exception {
        // 1. Admin creates Banner
        CreateBannerRequest bannerReq = new CreateBannerRequest(
                "The Botanical Longevity Collection",
                "Cellular Radiance Formulations",
                "https://cdn.elanor.com/banners/hero1.jpg",
                "/shop",
                "HOME_HERO",
                1,
                true
        );

        MvcResult bannerResult = mockMvc.perform(post("/api/v1/admin/cms/banners")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bannerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("The Botanical Longevity Collection"))
                .andReturn();

        // 2. Public lookup for HOME_HERO banners
        mockMvc.perform(get("/api/v1/cms/banners?placement=HOME_HERO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].placement").value("HOME_HERO"));
    }

    @Test
    public void testBlogPostLifecycle() throws Exception {
        String uniqueSlug = "cellular-botanique-art-" + UUID.randomUUID().toString().substring(0, 6);

        // 1. Admin creates Published Blog Post
        CreateBlogPostRequest blogReq = new CreateBlogPostRequest(
                "The Art of Cellular Botanique",
                uniqueSlug,
                "Exploring rare botanical actives in cellular regeneration.",
                "# Botanical Longevity\n\nNature and science synthesize into supreme skincare.",
                "https://cdn.elanor.com/blog/cover.jpg",
                "Dr. Vivienne Laurent",
                BlogPostStatus.PUBLISHED,
                Instant.now(),
                "Cellular Botanique - Élanor Journal",
                "Discover how rare alpine plants regenerate skin barrier."
        );

        mockMvc.perform(post("/api/v1/admin/blog/posts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value(uniqueSlug));

        // 2. Public fetches published blog posts
        mockMvc.perform(get("/api/v1/blog/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));

        // 3. Public fetches single article by slug
        mockMvc.perform(get("/api/v1/blog/posts/" + uniqueSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("The Art of Cellular Botanique"))
                .andExpect(jsonPath("$.data.author").value("Dr. Vivienne Laurent"));
    }

    @Test
    public void testCustomerForbiddenFromAdminCms() throws Exception {
        CreateBannerRequest bannerReq = new CreateBannerRequest(
                "Unauthorized Banner", "", "https://example.com/img.jpg", "/shop", "HOME_HERO", 1, true
        );

        mockMvc.perform(post("/api/v1/admin/cms/banners")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bannerReq)))
                .andExpect(status().isForbidden());
    }
}
