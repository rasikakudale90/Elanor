package com.elanor.ai.provider.impl;

import com.elanor.ai.dto.*;
import com.elanor.ai.provider.AiAssistantProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Component
@Primary
public class GeminiAiAssistantProvider implements AiAssistantProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAssistantProvider.class);

    private final String apiKey;
    private final String model;
    private final MockLocalAiAssistantProvider mockFallback;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public GeminiAiAssistantProvider(
            @Value("${elanor.ai.gemini.api-key:}") String apiKey,
            @Value("${elanor.ai.gemini.model:gemini-1.5-flash}") String model,
            MockLocalAiAssistantProvider mockFallback,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null && !model.isBlank() ? model.trim() : "gemini-1.5-flash";
        this.mockFallback = mockFallback;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getProviderName() {
        return (apiKey.isEmpty() || "mock".equalsIgnoreCase(apiKey)) ? "MOCK_LOCAL" : "GEMINI";
    }

    @Override
    public AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext) {
        return generateChatResponse(request, catalogContext, Collections.emptyList());
    }

    @Override
    public AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext, List<RetrievedChunkDto> retrievedEvidence) {
        // If no API key configured or test mock mode, seamlessly use our grounded local provider
        if (apiKey.isEmpty() || "mock".equalsIgnoreCase(apiKey)) {
            log.debug("No GEMINI_API_KEY configured; utilizing high-fidelity grounded local reasoning provider.");
            return mockFallback.generateChatResponse(request, catalogContext, retrievedEvidence);
        }

        try {
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);

            // Construct Grounded Prompt
            StringBuilder systemPrompt = new StringBuilder();
            systemPrompt.append("You are the Maison Élanor Haute Botanical Concierge. Élanor is an ultra-luxury French botanical skincare maison specializing in cellular longevity.\n");
            systemPrompt.append("Tone: Refined, warm, poetic yet clinically grounded. Address the patron with quiet luxury.\n");
            systemPrompt.append("GROUNDING CONSTRAINTS:\n");
            systemPrompt.append("1. Answer using ONLY the provided RETRIEVED EVIDENCE and CATALOG DATA below.\n");
            systemPrompt.append("2. If evidence is insufficient, state honestly: 'I do not have verified atelier records to answer that accurately.' Suggest consulting our concierge or a dermatologist.\n");
            systemPrompt.append("3. SAFETY: If asked to diagnose, cure, or treat skin disease (e.g., eczema, psoriasis, dermatitis, infections) or provide medical clearance for pregnancy/prescriptions, clearly explain that Élanor formulations are cosmetic and advise consulting a medical physician.\n");
            systemPrompt.append("4. Never invent prices, ingredients, or policies.\n\n");

            systemPrompt.append("--- RETRIEVED KNOWLEDGE BASE EVIDENCE ---\n");
            if (retrievedEvidence != null && !retrievedEvidence.isEmpty()) {
                for (RetrievedChunkDto chunk : retrievedEvidence) {
                    systemPrompt.append(String.format("[%s: %s]\n%s\n\n", chunk.getSourceType(), chunk.getTitle(), chunk.getContent()));
                }
            } else {
                systemPrompt.append("No specific external knowledge chunks retrieved.\n\n");
            }

            systemPrompt.append("--- ACTIVE CATALOG FORMULATIONS ---\n");
            if (catalogContext != null) {
                for (CatalogContextDto item : catalogContext) {
                    systemPrompt.append(String.format("Product: %s (Category: %s, Price: ₹%s, Summary: %s)\n",
                            item.getName(), item.getCategoryName(), item.getPrice(), item.getSubtitle()));
                }
            }

            Map<String, Object> contentsMap = new HashMap<>();
            contentsMap.put("role", "user");
            contentsMap.put("parts", List.of(Map.of("text", systemPrompt.toString() + "\n\nPatron Inquiry: " + request.getMessage())));

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(contentsMap),
                    "generationConfig", Map.of(
                            "temperature", 0.3,
                            "maxOutputTokens", 800
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                    return buildResponseFromGeminiOutput(rawText, request, catalogContext, retrievedEvidence);
                }
            }
        } catch (Exception e) {
            log.warn("Gemini API call failed or encountered error [{}]. Seamlessly falling back to local grounded reasoning provider.", e.getMessage());
        }

        return mockFallback.generateChatResponse(request, catalogContext, retrievedEvidence);
    }

    private AiChatResponse buildResponseFromGeminiOutput(
            String text,
            AiChatRequest request,
            List<CatalogContextDto> catalogContext,
            List<RetrievedChunkDto> retrievedEvidence) {

        List<KnowledgeCitationDto> citations = new ArrayList<>();
        if (retrievedEvidence != null) {
            for (RetrievedChunkDto chunk : retrievedEvidence) {
                String urlOrRef = "/";
                if ("PRODUCT".equalsIgnoreCase(chunk.getSourceType())) {
                    urlOrRef = "/product/" + chunk.getSourceId();
                } else if ("POLICY".equalsIgnoreCase(chunk.getSourceType())) {
                    urlOrRef = "shipping-policy".equals(chunk.getSourceId()) ? "/track" : "/policies/returns";
                }
                String excerpt = chunk.getContent().length() > 140
                        ? chunk.getContent().substring(0, 140) + "..."
                        : chunk.getContent();
                citations.add(new KnowledgeCitationDto(
                        chunk.getSourceType() + ":" + chunk.getSourceId(),
                        chunk.getTitle(),
                        urlOrRef,
                        excerpt.replace("\n", " ")
                ));
            }
        }

        List<ProductRecommendationDto> recommendations = new ArrayList<>();
        if (catalogContext != null) {
            for (CatalogContextDto item : catalogContext) {
                if (text.toLowerCase(Locale.ROOT).contains(item.getName().toLowerCase(Locale.ROOT))) {
                    recommendations.add(new ProductRecommendationDto(
                            item.getProductId(),
                            item.getVariantId(),
                            item.getName(),
                            item.getSubtitle(),
                            item.getPrice(),
                            item.getImageUrl(),
                            "Featured botanical formulation discussed in consultation."
                    ));
                }
            }
        }

        List<String> followUps = List.of(
                "How do I incorporate this into my morning ritual?",
                "What is your 7-day return and restocking policy?",
                "Can you check the dispatch status of my order?"
        );

        boolean needsHuman = text.toLowerCase(Locale.ROOT).contains("dermatologist") || text.toLowerCase(Locale.ROOT).contains("physician");
        String intent = needsHuman ? "SAFETY_ABSTENTION" : "RAG_CONSULTATION";

        return new AiChatResponse(text, recommendations, followUps, citations, needsHuman, intent);
    }

    @Override
    public AiConsultationResponse generateConsultation(AiConsultationRequest request, List<CatalogContextDto> catalogContext) {
        return mockFallback.generateConsultation(request, catalogContext);
    }
}
