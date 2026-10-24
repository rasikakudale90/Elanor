package com.elanor;

import com.elanor.ai.dto.KnowledgeCitationDto;
import com.elanor.ai.dto.RetrievedChunkDto;
import com.elanor.ai.entity.KnowledgeChunk;
import com.elanor.ai.repository.KnowledgeChunkRepository;
import com.elanor.ai.service.KnowledgeIngestionService;
import com.elanor.ai.service.KnowledgeRetrievalService;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeBaseRagTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private KnowledgeIngestionService knowledgeIngestionService;

    @Test
    @DisplayName("KnowledgeIngestionService should ingest catalog products, policies, and FAQs without error")
    void testIngestAllKnowledge() {
        Product sampleProduct = new Product();
        sampleProduct.setName("Baume Apaisant Réparateur");
        sampleProduct.setSlug("baume-apaisant");
        sampleProduct.setShortDescription("Cellular soothing balm");
        sampleProduct.setDescription("A luxurious restorative balm for sensitized skin.");
        sampleProduct.setBasePrice(new BigDecimal("120.00"));
        sampleProduct.setStatus(ProductStatus.ACTIVE);

        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));
        when(productVariantRepository.findByProduct(any())).thenReturn(Collections.emptyList());
        when(knowledgeChunkRepository.findBySourceTypeAndSourceId(any(), any())).thenReturn(Optional.empty());

        knowledgeIngestionService.ingestCatalogProducts();
        knowledgeIngestionService.ingestBrandPolicies();
        knowledgeIngestionService.ingestSkincareFaq();

        // 1 product + 3 policies + 2 FAQs = 6 saves
        verify(knowledgeChunkRepository, atLeast(6)).save(any(KnowledgeChunk.class));
    }

    @Test
    @DisplayName("KnowledgeRetrievalService should retrieve relevant policy chunk and format citation")
    void testRetrieveRelevantPolicy() {
        KnowledgeChunk returnPolicyChunk = new KnowledgeChunk(
                "POLICY",
                "return-policy",
                "Maison Élanor 7-Day Return & Restocking Policy",
                "Patrons may initiate returns or replacements within 7 calendar days of confirmed delivery.",
                "{\"url\":\"/policies/returns\"}",
                "hash123"
        );

        KnowledgeRetrievalService retrievalService = new KnowledgeRetrievalService(knowledgeChunkRepository);
        when(knowledgeChunkRepository.findAll()).thenReturn(List.of(returnPolicyChunk));

        List<RetrievedChunkDto> results = retrievalService.retrieveRelevant("What is your 7 day return policy?", 3, 0.20);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).contains("7-Day Return");

        KnowledgeCitationDto citation = retrievalService.toCitation(results.get(0));
        assertThat(citation.getSourceId()).isEqualTo("POLICY:return-policy");
        assertThat(citation.getUrlOrReference()).isEqualTo("/policies/returns");
        assertThat(citation.getExcerpt()).contains("7 calendar days");
    }
}
