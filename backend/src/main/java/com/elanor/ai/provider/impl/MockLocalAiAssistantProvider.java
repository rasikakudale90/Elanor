package com.elanor.ai.provider.impl;

import com.elanor.ai.dto.*;
import com.elanor.ai.provider.AiAssistantProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MockLocalAiAssistantProvider implements AiAssistantProvider {

    @Override
    public String getProviderName() {
        return "MOCK_LOCAL";
    }

    @Override
    public AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext) {
        return generateChatResponse(request, catalogContext, List.of());
    }

    @Override
    public AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext, List<RetrievedChunkDto> retrievedEvidence) {
        String msg = request.getMessage() != null ? request.getMessage().toLowerCase(Locale.ROOT) : "";
        List<ProductRecommendationDto> recommendations = new ArrayList<>();
        List<String> followUps = new ArrayList<>();
        List<KnowledgeCitationDto> citations = new ArrayList<>();
        boolean needsHumanSupport = false;
        String intent = "GENERAL_CHAT";
        String reply;

        // Map citations from retrieved evidence
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

        // 1. Skincare Medical / Cure Safety Guardrail
        if (msg.contains("cure") || msg.contains("psoriasis") || msg.contains("dermatitis") || msg.contains("treat disease") || msg.contains("infection")) {
            reply = "While Maison Élanor formulations are crafted with rare, clinical-grade botanicals to support barrier resilience and cellular longevity, our formulations are cosmetic and not formulated to diagnose, cure, or treat dermatological diseases. We respectfully advise consulting a board-certified dermatologist for medical conditions.";
            needsHumanSupport = true;
            intent = "SAFETY_ABSTENTION";
            followUps.add("Would you like to explore gentle, non-stripping soothing balms?");
            followUps.add("How do I conduct a 24-hour patch test?");
            return new AiChatResponse(reply, recommendations, followUps, citations, needsHumanSupport, intent);
        }

        // 2. Returns & Refunds Policy
        if (msg.contains("return") || msg.contains("refund") || msg.contains("exchange") || msg.contains("replace")) {
            reply = "Under Maison Élanor's 7-Day Sacred Return Policy, patrons may request returns or replacements within 7 calendar days of verified delivery. Formulations must remain in pristine original packaging with at least 90% uncompromised volume. Following a 48-hour atelier inspection, approved refunds are credited within 3-5 business days.";
            intent = "POLICY_FAQ";
            followUps.add("How do I initiate a return for my order?");
            followUps.add("Can I cancel my order before it ships?");
        }
        // 3. Shipping & Logistics Policy
        else if (msg.contains("shipping") || msg.contains("delivery") || msg.contains("dispatch") || msg.contains("courier") || msg.contains("cost")) {
            reply = "Maison Élanor provides complimentary white-glove express delivery on all orders exceeding ₹1,500 ($150 USD). Standard delivery is ₹150. Dispatches are handled via BlueDart Express and our Shiprocket logistics network, delivering within 3 to 5 business days with live 5-stage milestone tracking.";
            intent = "POLICY_FAQ";
            followUps.add("Track my recent order dispatch");
            followUps.add("What countries do you deliver to?");
        }
        // 4. Formulation & Product Inquiries (Balm / Barrier / Soothing)
        else if (msg.contains("balm") || msg.contains("barrier") || msg.contains("dry") || msg.contains("soothing") || msg.contains("baume")) {
            reply = "For barrier reinforcement, our Baume Apaisant Réparateur is infused with blue tansy, centella asiatica, and botanical squalane to immediately calm inflammation and seal in essential hydration.";
            intent = "PRODUCT_INQUIRY";
            CatalogContextDto found = findCatalogItem(catalogContext, "balm", "soothing");
            if (found != null) {
                recommendations.add(new ProductRecommendationDto(
                        found.getProductId(),
                        found.getVariantId(),
                        found.getName(),
                        found.getSubtitle(),
                        found.getPrice(),
                        found.getImageUrl(),
                        "Clinical lipid replenishment for compromised or dry skin."
                ));
            }
            followUps.add("Can I layer the Soothing Balm over retinol treatments?");
            followUps.add("What is the difference between the balm and the elixir?");
        }
        // 5. Sensitive Skin & Patch Testing Protocol
        else if (msg.contains("patch test") || msg.contains("allergy") || msg.contains("react") || msg.contains("sensitive")) {
            reply = "For sensitized or reactive skin types, we recommend a 24-hour patch test on the inner forearm or behind the ear prior to full facial ritual application. If any irritation arises, gently cleanse with cool water and discontinue use.";
            intent = "ROUTINE_ADVICE";
            followUps.add("Which Élanor products are best for compromised barriers?");
            followUps.add("Tell me more about Baume Apaisant Réparateur.");
        } else if (msg.contains("serum") || msg.contains("aging") || msg.contains("wrinkle") || msg.contains("cellular")) {
            reply = "Our flagship Cellular Youth Serum harnesses rare alpine edelweiss stem cells and multi-molecular hyaluronic acid to visibly re-densify cellular skin matrix and enhance dermal elasticity.";
            intent = "PRODUCT_INQUIRY";
            CatalogContextDto found = findCatalogItem(catalogContext, "serum", "cellular");
            if (found != null) {
                recommendations.add(new ProductRecommendationDto(
                        found.getProductId(),
                        found.getVariantId(),
                        found.getName(),
                        found.getSubtitle(),
                        found.getPrice(),
                        found.getImageUrl(),
                        "Award-winning cellular regeneration with 94% bio-available peptides."
                ));
            }
            followUps.add("How do I incorporate the Cellular Youth Serum into my PM ritual?");
            followUps.add("Is this formula suitable for sensitive barrier types?");
        } else if (msg.contains("elixir") || msg.contains("glow") || msg.contains("radiance") || msg.contains("oil")) {
            reply = "The Botanical Longevity Elixir combines cold-pressed camellia seed and rosehip oils with botanical retinol alternative Bakuchiol to deliver instantaneous luminosity without greasy residue.";
            intent = "PRODUCT_INQUIRY";
            CatalogContextDto found = findCatalogItem(catalogContext, "elixir", "oil");
            if (found != null) {
                recommendations.add(new ProductRecommendationDto(
                        found.getProductId(),
                        found.getVariantId(),
                        found.getName(),
                        found.getSubtitle(),
                        found.getPrice(),
                        found.getImageUrl(),
                        "Pure botanical lipid matrix for luminous cellular vitality."
                ));
            }
            followUps.add("Should I apply the elixir before or after my moisturizer?");
            followUps.add("Is Bakuchiol pregnancy-safe?");
        } else {
            reply = "Welcome to Élanor Haute Skin Concierge. Our bespoke formulations blend precious botanical extracts with cellular longevity science. How may I assist your dermal ritual today?";
            if (!catalogContext.isEmpty()) {
                CatalogContextDto first = catalogContext.get(0);
                recommendations.add(new ProductRecommendationDto(
                        first.getProductId(),
                        first.getVariantId(),
                        first.getName(),
                        first.getSubtitle(),
                        first.getPrice(),
                        first.getImageUrl(),
                        "Handcrafted signature botanical formulation."
                ));
            }
            followUps.add("Take the 4-step Haute Skin Diagnostic consultation");
            followUps.add("Which formulation is recommended for dehydration?");
        }

        return new AiChatResponse(reply, recommendations, followUps, citations, needsHumanSupport, intent);
    }

    @Override
    public AiConsultationResponse generateConsultation(AiConsultationRequest request, List<CatalogContextDto> catalogContext) {
        String skinType = request.getSkinType() != null ? request.getSkinType().toUpperCase(Locale.ROOT) : "COMBINATION";
        List<String> concerns = request.getPrimaryConcerns() != null ? request.getPrimaryConcerns() : List.of("HYDRATION");

        // Compute diagnostic scores
        int hydration = computeHydration(skinType, concerns);
        int barrier = computeBarrier(skinType, concerns);
        int radiance = computeRadiance(concerns);
        int reactivity = computeReactivity(skinType, concerns);

        VitalityScoreDto scores = new VitalityScoreDto(
                hydration,
                barrier,
                radiance,
                reactivity,
                String.format("Dermal assessment indicates %s baseline phenotype with priority focus on %s.",
                        skinType, String.join(", ", concerns))
        );

        List<RitualStepDto> morningRitual = new ArrayList<>();
        List<RitualStepDto> eveningRitual = new ArrayList<>();

        CatalogContextDto defaultItem = catalogContext.isEmpty() ? null : catalogContext.get(0);
        CatalogContextDto serumItem = findCatalogItem(catalogContext, "serum", "youth", "cellular");
        CatalogContextDto elixirItem = findCatalogItem(catalogContext, "elixir", "oil", "longevity");
        CatalogContextDto balmItem = findCatalogItem(catalogContext, "balm", "soothing", "cream");

        if (serumItem == null) serumItem = defaultItem;
        if (elixirItem == null) elixirItem = defaultItem;
        if (balmItem == null) balmItem = defaultItem;

        // Morning Ritual
        morningRitual.add(new RitualStepDto(
                1,
                "Cleanse & Prepare",
                "MORNING",
                serumItem != null ? serumItem.getProductId() : UUID.randomUUID(),
                serumItem != null ? serumItem.getVariantId() : UUID.randomUUID(),
                serumItem != null ? serumItem.getName() : "Cellular Activation Essence",
                serumItem != null ? serumItem.getSubtitle() : "Bio-Active Botanical Hydrosol",
                serumItem != null ? serumItem.getPrice() : new BigDecimal("2400.00"),
                serumItem != null ? serumItem.getImageUrl() : "/images/products/skin1.png",
                "Warm 3 drops between palms and gently press onto damp complexion in upward rhythmic motions.",
                "Infuses high-potency cellular antioxidants to protect against urban oxidative stressors."
        ));

        morningRitual.add(new RitualStepDto(
                2,
                "Luminosity & Lipid Shield",
                "MORNING",
                elixirItem != null ? elixirItem.getProductId() : UUID.randomUUID(),
                elixirItem != null ? elixirItem.getVariantId() : UUID.randomUUID(),
                elixirItem != null ? elixirItem.getName() : "Botanical Longevity Elixir",
                elixirItem != null ? elixirItem.getSubtitle() : "Alpine Micro-Flora Nectar",
                elixirItem != null ? elixirItem.getPrice() : new BigDecimal("3800.00"),
                elixirItem != null ? elixirItem.getImageUrl() : "/images/products/skin4.png",
                "Glide along cheekbones and forehead with gentle lymphatic drainage sweeps.",
                "Reinforces stratum corneum barrier and delivers all-day dewy radiance."
        ));

        // Evening Ritual
        eveningRitual.add(new RitualStepDto(
                1,
                "Intensive Cellular Repair",
                "EVENING",
                serumItem != null ? serumItem.getProductId() : UUID.randomUUID(),
                serumItem != null ? serumItem.getVariantId() : UUID.randomUUID(),
                serumItem != null ? serumItem.getName() : "Cellular Youth Serum",
                serumItem != null ? serumItem.getSubtitle() : "Concentré Botanique Régénérant",
                serumItem != null ? serumItem.getPrice() : new BigDecimal("4500.00"),
                serumItem != null ? serumItem.getImageUrl() : "/images/products/skin1.png",
                "Dispense one full pipette over face, neck, and décolletage before resting.",
                "Accelerates nocturnal fibroblast activation and mitochondrial renewal."
        ));

        eveningRitual.add(new RitualStepDto(
                2,
                "Deep Lipid Sealing Barrier",
                "EVENING",
                balmItem != null ? balmItem.getProductId() : UUID.randomUUID(),
                balmItem != null ? balmItem.getVariantId() : UUID.randomUUID(),
                balmItem != null ? balmItem.getName() : "Baume Apaisant Réparateur",
                balmItem != null ? balmItem.getSubtitle() : "Centella & Rare Botanical Balm",
                balmItem != null ? balmItem.getPrice() : new BigDecimal("3200.00"),
                balmItem != null ? balmItem.getImageUrl() : "/images/products/skin8.png",
                "Melt a pearl-sized amount between fingertips and massage gently across delicate areas.",
                "Prevents transepidermal water loss (TEWL) during circadian sleep cycles."
        ));

        // Calculate pricing & discount
        BigDecimal subtotal = BigDecimal.ZERO;
        for (RitualStepDto step : morningRitual) {
            if (step.getPrice() != null) subtotal = subtotal.add(step.getPrice());
        }
        for (RitualStepDto step : eveningRitual) {
            if (step.getPrice() != null) subtotal = subtotal.add(step.getPrice());
        }

        BigDecimal discountPct = new BigDecimal("15.00");
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(new BigDecimal("0.15"));
        BigDecimal bundledPrice = subtotal.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);

        return new AiConsultationResponse(
                "Bespoke Haute Botanical Longevity Prescription",
                skinType + " / " + (request.getClimate() != null ? request.getClimate() : "TEMPERATE"),
                scores,
                morningRitual,
                eveningRitual,
                subtotal,
                discountPct,
                bundledPrice,
                "Prescription formulated exclusively according to Élanor Clinical Botanical Protocols. Includes complimentary 15% bespoke concierge ritual benefit."
        );
    }

    private CatalogContextDto findCatalogItem(List<CatalogContextDto> catalog, String... keywords) {
        if (catalog == null || catalog.isEmpty()) return null;
        for (CatalogContextDto item : catalog) {
            String combined = (item.getName() + " " + item.getSubtitle() + " " + item.getDescription() + " " + item.getSlug()).toLowerCase(Locale.ROOT);
            for (String kw : keywords) {
                if (combined.contains(kw.toLowerCase(Locale.ROOT))) {
                    return item;
                }
            }
        }
        return catalog.get(0);
    }

    private int computeHydration(String skinType, List<String> concerns) {
        int base = 65;
        if ("DRY".equalsIgnoreCase(skinType)) base = 42;
        if ("OILY".equalsIgnoreCase(skinType)) base = 78;
        if (concerns.stream().anyMatch(c -> c.toUpperCase().contains("HYDRAT"))) base = Math.max(35, base - 20);
        return Math.min(98, Math.max(25, base));
    }

    private int computeBarrier(String skinType, List<String> concerns) {
        int base = 70;
        if ("SENSITIVE".equalsIgnoreCase(skinType)) base = 48;
        if (concerns.stream().anyMatch(c -> c.toUpperCase().contains("BARRIER") || c.toUpperCase().contains("REDNESS"))) base = Math.max(40, base - 25);
        return Math.min(95, Math.max(30, base));
    }

    private int computeRadiance(List<String> concerns) {
        int base = 75;
        if (concerns.stream().anyMatch(c -> c.toUpperCase().contains("DULL") || c.toUpperCase().contains("PIGMENT"))) base = 52;
        return base;
    }

    private int computeReactivity(String skinType, List<String> concerns) {
        int base = 80;
        if ("SENSITIVE".equalsIgnoreCase(skinType)) base = 45;
        return base;
    }
}
