package com.elanor.ai.service;

import com.elanor.ai.entity.KnowledgeChunk;
import com.elanor.ai.repository.KnowledgeChunkRepository;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeIngestionService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeIngestionService.class);

    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public KnowledgeIngestionService(
            KnowledgeChunkRepository knowledgeChunkRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        log.info("Initializing Élanor RAG Knowledge Ingestion Pipeline...");
        ingestCatalogProducts();
        ingestBrandPolicies();
        ingestSkincareFaq();
        log.info("Élanor RAG Knowledge Base ready with {} verified chunks.", knowledgeChunkRepository.count());
    }

    @Transactional
    public void ingestCatalogProducts() {
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .toList();

        for (Product product : products) {
            ingestProduct(product);
        }
    }

    @Transactional
    public void ingestProduct(Product product) {
        if (product == null || product.getStatus() != ProductStatus.ACTIVE) {
            return;
        }

        List<ProductVariant> variants = productVariantRepository.findByProduct(product);
        String variantInfo = variants.stream()
                .map(v -> String.format("%s (SKU: %s, Price: ₹%s)",
                        v.getName() != null ? v.getName() : "Standard",
                        v.getSku(),
                        v.getPrice()))
                .collect(java.util.stream.Collectors.joining("; "));
        if (variantInfo.isEmpty()) {
            variantInfo = "Standard atelier bottle";
        }

        String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Botanical Care";

        StringBuilder content = new StringBuilder();
        content.append("Product Name: ").append(product.getName()).append("\n");
        content.append("Category: ").append(categoryName).append("\n");
        content.append("Summary: ").append(product.getShortDescription() != null ? product.getShortDescription() : "").append("\n");
        content.append("Detailed Clinical Description: ").append(product.getDescription() != null ? product.getDescription() : "").append("\n");
        content.append("Formulation Variants & Pricing: ").append(variantInfo).append("\n");
        content.append("Atelier Craftsmanship: Handcrafted in France using cold-pressed botanical actives, certified organic cellular nutrients, and zero synthetic parabens.\n");
        content.append("Application Protocol: Warm 2-3 drops between fingertips, press gently onto cleansed skin across face, neck, and décolleté in upward sweeping motions.\n");

        String hash = computeHash(content.toString());
        upsertChunk("PRODUCT", product.getSlug(), product.getName() + " — Botanical Profile", content.toString(),
                String.format("{\"slug\":\"%s\",\"category\":\"%s\",\"basePrice\":%s}", product.getSlug(), categoryName, product.getBasePrice()), hash);
    }

    @Transactional
    public void ingestBrandPolicies() {
        // 1. Returns & Refunds Policy
        String returnPolicy = """
                Policy: Élanor 7-Day Sacred Return & Refund Window
                Eligibility: Patrons may initiate returns or replacements within 7 calendar days of confirmed delivery.
                Condition: Formulations must remain in original wax-sealed or pristine packaging with at least 90% volume uncompromised for hygiene safety.
                Inspection: Returned parcels undergo a 48-hour atelier inspection. Once approved, refunds are credited back to the original payment instrument within 3-5 business days.
                Pre-Shipment Cancellation: Orders in CREATED, CONFIRMED, or PROCESSING state may be cancelled immediately from the customer sanctuary for a full automatic refund before dispatch.
                """;
        upsertChunk("POLICY", "return-policy", "Maison Élanor 7-Day Return & Restocking Policy", returnPolicy,
                "{\"url\":\"/policies/returns\",\"category\":\"Policy\"}", computeHash(returnPolicy));

        // 2. Shipping & White-Glove Logistics
        String shippingPolicy = """
                Policy: Élanor Sacred Dispatch & White-Glove Shipping
                Complimentary Tier: Complimentary worldwide insured delivery on all orders exceeding ₹1,500 (or $150 USD). Standard flat shipping of ₹150 applies below this threshold.
                Carriers & Speed: Dispatched from our central atelier via BlueDart Express and Shiprocket logistics partner network. Estimated delivery is 3 to 5 business days.
                Live Tracking: Each dispatched parcel receives a unique BlueDart / Shiprocket Air Waybill (AWB) allowing real-time 5-stage milestone tracking from atelier wax seal to patron signature.
                """;
        upsertChunk("POLICY", "shipping-policy", "Maison Élanor White-Glove Shipping & Delivery Policy", shippingPolicy,
                "{\"url\":\"/track\",\"category\":\"Policy\"}", computeHash(shippingPolicy));

        // 3. Brand Essence & Sustainability
        String brandEssence = """
                Brand: Maison Élanor — Pure Beauty. Naturally.
                Essence: Haute Botanique and Clinical Cellular Longevity.
                Philosophy: We harmonize rare high-altitude botanical extracts with dermatological biotechnology. Every harvest is sustainably sourced, cruelty-free, vegan, and packaged in recyclable biophotonic glass.
                """;
        upsertChunk("BRAND", "brand-essence", "Maison Élanor Brand Philosophy & Purity Standards", brandEssence,
                "{\"url\":\"/about\",\"category\":\"Brand\"}", computeHash(brandEssence));
    }

    @Transactional
    public void ingestSkincareFaq() {
        // FAQ 1: Sensitive Skin & Patch Testing
        String patchTest = """
                Clinical Guidance: Sensitive Skin & Patch Testing Protocol
                General Advice: While Élanor formulas are hypoallergenic and dermatologically evaluated, patrons with hypersensitive skin or reactive eczema are advised to conduct a 24-hour patch test behind the ear or inner forearm prior to full facial ritual application.
                Reactivity: In the rare event of erythema or irritation, cleanse skin immediately with cool water and discontinue use. For persistent reactions, consult a dermatologist.
                """;
        upsertChunk("FAQ", "patch-testing", "Sensitive Skin Protocol & Patch Testing Guidelines", patchTest,
                "{\"category\":\"Dermatological FAQ\"}", computeHash(patchTest));

        // FAQ 2: Morning vs Evening Routine Sequencing
        String sequencing = """
                Ritual Sequencing: Morning (AM) vs Evening (PM) Protocol
                Morning Order: 1. Gentle Botanical Cleanser -> 2. Antioxidant Cellular Essence/Toner -> 3. Targeted Active Serum (e.g., Vitamin C/Niacinamide) -> 4. Lightweight Hydrating Day Crème -> 5. Mineral SPF Protection.
                Evening Order: 1. Deep Oil/Balm Cleanser -> 2. Nourishing Botanical Elixir -> 3. Restorative Night Lipid Balm (e.g., Baume Apaisant Réparateur) to seal transepidermal moisture during circadian cellular repair.
                """;
        upsertChunk("FAQ", "ritual-sequencing", "Morning & Evening Botanical Ritual Sequencing Protocol", sequencing,
                "{\"category\":\"Application Guide\"}", computeHash(sequencing));
    }

    private void upsertChunk(String sourceType, String sourceId, String title, String content, String metadataJson, String hash) {
        Optional<KnowledgeChunk> existing = knowledgeChunkRepository.findBySourceTypeAndSourceId(sourceType, sourceId);
        if (existing.isPresent()) {
            KnowledgeChunk chunk = existing.get();
            if (!hash.equals(chunk.getContentHash())) {
                chunk.setTitle(title);
                chunk.setContent(content);
                chunk.setMetadataJson(metadataJson);
                chunk.setContentHash(hash);
                knowledgeChunkRepository.save(chunk);
                log.info("Updated knowledge chunk [{} : {}]", sourceType, sourceId);
            }
        } else {
            KnowledgeChunk chunk = new KnowledgeChunk(sourceType, sourceId, title, content, metadataJson, hash);
            knowledgeChunkRepository.save(chunk);
            log.info("Ingested new knowledge chunk [{} : {}]", sourceType, sourceId);
        }
    }

    private String computeHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(text.hashCode());
        }
    }
}
