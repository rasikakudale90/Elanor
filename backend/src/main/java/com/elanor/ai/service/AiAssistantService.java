package com.elanor.ai.service;

import com.elanor.ai.dto.*;
import com.elanor.ai.provider.AiAssistantProvider;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiAssistantService {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantService.class);

    private final AiAssistantProvider aiAssistantProvider;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public AiAssistantService(
            AiAssistantProvider aiAssistantProvider,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {
        this.aiAssistantProvider = aiAssistantProvider;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Transactional(readOnly = true)
    public AiChatResponse chat(AiChatRequest request) {
        List<CatalogContextDto> catalogContext = buildCatalogContext();
        log.info("Processing AI chat inquiry with {} active catalog context items", catalogContext.size());
        return aiAssistantProvider.generateChatResponse(request, catalogContext);
    }

    @Transactional(readOnly = true)
    public AiConsultationResponse consult(AiConsultationRequest request) {
        List<CatalogContextDto> catalogContext = buildCatalogContext();
        log.info("Generating Haute AI Skin Consultation for phenotype [{}] with {} active catalog context items",
                request.getSkinType(), catalogContext.size());
        return aiAssistantProvider.generateConsultation(request, catalogContext);
    }

    private List<CatalogContextDto> buildCatalogContext() {
        List<CatalogContextDto> list = new ArrayList<>();
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .toList();

        for (Product p : products) {
            List<ProductVariant> variants = productVariantRepository.findByProduct(p);
            ProductVariant primaryVariant = variants.isEmpty() ? null : variants.get(0);

            String imgUrl = (p.getMedia() != null && !p.getMedia().isEmpty())
                    ? p.getMedia().get(0).getMediaUrl()
                    : "/images/products/skin1.png";

            list.add(new CatalogContextDto(
                    p.getId(),
                    primaryVariant != null ? primaryVariant.getId() : null,
                    p.getName(),
                    p.getShortDescription(),
                    p.getSlug(),
                    p.getDescription(),
                    p.getCategory() != null ? p.getCategory().getName() : "Botanical Care",
                    primaryVariant != null ? primaryVariant.getPrice() : p.getBasePrice(),
                    imgUrl,
                    primaryVariant != null && primaryVariant.isActive()
            ));
        }
        return list;
    }
}
