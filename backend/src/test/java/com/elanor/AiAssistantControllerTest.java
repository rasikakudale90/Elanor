package com.elanor;

import com.elanor.ai.dto.AiChatRequest;
import com.elanor.ai.dto.AiConsultationRequest;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AiAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @BeforeEach
    public void setup() {
        Product p = new Product();
        p.setName("Cellular Youth Serum");
        p.setShortDescription("Concentré Botanique Régénérant");
        p.setDescription("Flagship cellular longevity serum with alpine edelweiss stem cells.");
        p.setSlug("cellular-youth-serum-" + UUID.randomUUID().toString().substring(0, 6));
        p.setStatus(ProductStatus.ACTIVE);
        p.setBasePrice(new BigDecimal("4500.00"));
        Product savedP = productRepository.save(p);

        ProductVariant v = new ProductVariant();
        v.setProduct(savedP);
        v.setSku("SERUM-30ML-" + UUID.randomUUID().toString().substring(0, 6));
        v.setName("30ml Dispenser");
        v.setPrice(new BigDecimal("4500.00"));
        v.setActive(true);
        productVariantRepository.save(v);
    }

    @Test
    public void testAiChatInquiry_SerumGroundedResponse() throws Exception {
        AiChatRequest request = new AiChatRequest();
        request.setMessage("Can you tell me more about your anti-aging serum and wrinkles?");

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reply", containsString("Cellular Youth Serum")))
                .andExpect(jsonPath("$.data.suggestedProducts", not(empty())))
                .andExpect(jsonPath("$.data.followUpQuestions", not(empty())));
    }

    @Test
    public void testAiChatInquiry_BarrierGroundedResponse() throws Exception {
        AiChatRequest request = new AiChatRequest();
        request.setMessage("What do you recommend for soothing dry sensitive barrier?");

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reply", containsString("Baume Apaisant Réparateur")))
                .andExpect(jsonPath("$.data.followUpQuestions", not(empty())));
    }

    @Test
    public void testAiConsultationDiagnosticQuiz_VitalityScoresAndRituals() throws Exception {
        AiConsultationRequest request = new AiConsultationRequest(
                "DRY",
                List.of("HYDRATION", "FINE_LINES"),
                "ARID",
                "COMPLETE_CELLULAR_RITUAL",
                "RICH_BALM",
                32
        );

        mockMvc.perform(post("/api/v1/ai/consult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.prescriptionTitle").value("Bespoke Haute Botanical Longevity Prescription"))
                .andExpect(jsonPath("$.data.vitalityScores.hydration").exists())
                .andExpect(jsonPath("$.data.vitalityScores.barrierIntegrity").exists())
                .andExpect(jsonPath("$.data.morningRitual", hasSize(2)))
                .andExpect(jsonPath("$.data.eveningRitual", hasSize(2)))
                .andExpect(jsonPath("$.data.bundledDiscountPercentage").value(15.00))
                .andExpect(jsonPath("$.data.bundledPrice").exists());
    }
}
