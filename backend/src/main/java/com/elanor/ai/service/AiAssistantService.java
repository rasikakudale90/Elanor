package com.elanor.ai.service;

import com.elanor.ai.dto.*;
import com.elanor.ai.provider.AiAssistantProvider;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.entity.ProductVariant;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.catalog.repository.ProductVariantRepository;
import com.elanor.common.security.UserPrincipal;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import com.elanor.order.entity.Order;
import com.elanor.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AiAssistantService {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantService.class);

    private final AiAssistantProvider aiAssistantProvider;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final OrderRepository orderRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public AiAssistantService(
            AiAssistantProvider aiAssistantProvider,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            KnowledgeRetrievalService knowledgeRetrievalService,
            OrderRepository orderRepository,
            CustomerProfileRepository customerProfileRepository) {
        this.aiAssistantProvider = aiAssistantProvider;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.orderRepository = orderRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    @Transactional(readOnly = true)
    public AiChatResponse chat(AiChatRequest request) {
        String msgLower = request.getMessage() != null ? request.getMessage().toLowerCase(Locale.ROOT) : "";

        // 1. Live Order Tracking Security Guardrail & Tool
        if (msgLower.contains("order") || msgLower.contains("track") || msgLower.contains("dispatch status") || msgLower.contains("eln-")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                CustomerProfile customer = customerProfileRepository.findByUserId(principal.getId()).orElse(null);
                if (customer != null) {
                    Page<Order> orders = orderRepository.findByCustomerOrderByCreatedAtDesc(customer, Pageable.ofSize(3));
                    if (!orders.isEmpty()) {
                        Order latest = orders.getContent().get(0);
                        String reply = String.format("Cher Patron, your sacred order [%s] is currently in status: %s. Total order value: ₹%s. Tracking is actively synchronized via BlueDart Express.",
                                latest.getOrderNumber(), latest.getStatus(), latest.getTotalAmount());
                        return new AiChatResponse(
                                reply,
                                List.of(),
                                List.of("View complete tracking timeline", "Browse restorative botanical balms"),
                                List.of(new KnowledgeCitationDto("ORDER:" + latest.getOrderNumber(), "Order " + latest.getOrderNumber(), "/track", "Milestone Status: " + latest.getStatus())),
                                false,
                                "ORDER_TRACKING"
                        );
                    } else {
                        return new AiChatResponse(
                                "Cher Patron, we found no active orders placed under your account yet. When you place a sacred order, you can monitor its 5-stage dispatch right here.",
                                List.of(),
                                List.of("Discover The Iconic Formulations", "Take the Haute Skin Diagnostic"),
                                List.of(),
                                false,
                                "ORDER_TRACKING"
                        );
                    }
                }
            } else {
                // Anonymous guest: Zero IDOR enforcement
                return new AiChatResponse(
                        "To protect your privacy and view confidential order milestones, please sign in to your Maison Élanor account via the Patron Sanctuary icon in the top navigation. Once authenticated, I will retrieve your real-time tracking timeline instantly.",
                        List.of(),
                        List.of("Sign in to Patron Sanctuary", "View complimentary shipping thresholds"),
                        List.of(new KnowledgeCitationDto("POLICY:shipping-policy", "Maison Élanor Dispatch Policy", "/track", "Complimentary delivery on orders exceeding ₹1,500 via BlueDart Express.")),
                        false,
                        "AUTH_REQUIRED"
                );
            }
        }

        // 2. Retrieve Grounded Knowledge Evidence
        List<RetrievedChunkDto> retrievedEvidence = knowledgeRetrievalService.retrieveRelevant(request.getMessage(), 4, 0.20);
        List<CatalogContextDto> catalogContext = buildCatalogContext();

        log.info("Processing AI chat inquiry with {} retrieved evidence chunks and {} catalog items",
                retrievedEvidence.size(), catalogContext.size());

        return aiAssistantProvider.generateChatResponse(request, catalogContext, retrievedEvidence);
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
