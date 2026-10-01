package com.elanor.search.service;

import com.elanor.catalog.dto.ProductSummaryDto;
import com.elanor.catalog.entity.Product;
import com.elanor.catalog.entity.ProductMedia;
import com.elanor.catalog.entity.ProductStatus;
import com.elanor.catalog.repository.ProductRepository;
import com.elanor.category.dto.CategoryDto;
import com.elanor.category.entity.Category;
import com.elanor.category.repository.CategoryRepository;
import com.elanor.collection.entity.Collection;
import com.elanor.search.dto.SearchFilterRequest;
import com.elanor.search.dto.SearchSuggestionDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SearchService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDto> search(SearchFilterRequest request) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Instant now = Instant.now();

            // 1. Status & scheduled publishing window
            Predicate isActive = cb.equal(root.get("status"), ProductStatus.ACTIVE);
            Predicate isScheduled = cb.and(
                    cb.equal(root.get("status"), ProductStatus.SCHEDULED),
                    cb.or(cb.isNull(root.get("publishAt")), cb.lessThanOrEqualTo(root.get("publishAt"), now)),
                    cb.or(cb.isNull(root.get("unpublishAt")), cb.greaterThan(root.get("unpublishAt"), now))
            );
            predicates.add(cb.or(isActive, isScheduled));

            // 2. Keyword query
            if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
                String pattern = "%" + request.getQuery().trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate shortDescMatch = cb.like(cb.lower(root.get("shortDescription")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate badgeMatch = cb.like(cb.lower(root.get("badges")), pattern);
                predicates.add(cb.or(nameMatch, shortDescMatch, descMatch, badgeMatch));
            }

            // 3. Category filter
            if (request.getCategorySlug() != null && !request.getCategorySlug().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("category").get("slug"), request.getCategorySlug().trim()));
            }

            // 4. Collection filter
            if (request.getCollectionSlug() != null && !request.getCollectionSlug().trim().isEmpty()) {
                Join<Product, Collection> collectionJoin = root.join("collections", JoinType.INNER);
                predicates.add(cb.equal(collectionJoin.get("slug"), request.getCollectionSlug().trim()));
            }

            // 5. Min / Max Price
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), request.getMaxPrice()));
            }

            // 6. Badges filter
            if (request.getBadge() != null && !request.getBadge().trim().isEmpty()) {
                String badgePattern = "%" + request.getBadge().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("badges")), badgePattern));
            }

            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = resolveSort(request.getSortBy());
        int page = Math.max(0, request.getPage());
        int size = request.getSize() > 0 ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(spec, pageable).map(this::mapToSummaryDto);
    }

    @Transactional(readOnly = true)
    public SearchSuggestionDto getSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return new SearchSuggestionDto(query, List.of(), List.of(), List.of());
        }

        String cleanQuery = query.trim();
        Instant now = Instant.now();

        // 1. Top matching products
        Pageable topFive = PageRequest.of(0, 5);
        List<Product> products = productRepository.findTopSuggestions(cleanQuery, now, topFive);
        List<ProductSummaryDto> productDtos = products.stream().map(this::mapToSummaryDto).collect(Collectors.toList());

        // 2. Top matching categories
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(cleanQuery);
        List<CategoryDto> categoryDtos = categories.stream().limit(3).map(this::mapCategoryToDto).collect(Collectors.toList());

        // 3. Extracted textual suggestions
        Set<String> suggestionStrings = new LinkedHashSet<>();
        for (Product p : products) {
            suggestionStrings.add(p.getName());
        }
        for (Category c : categories) {
            suggestionStrings.add(c.getName());
        }

        return new SearchSuggestionDto(cleanQuery, new ArrayList<>(suggestionStrings), productDtos, categoryDtos);
    }

    private ProductSummaryDto mapToSummaryDto(Product product) {
        String primaryImageUrl = null;
        if (product.getMedia() != null && !product.getMedia().isEmpty()) {
            primaryImageUrl = product.getMedia().stream()
                    .filter(ProductMedia::isCover)
                    .findFirst()
                    .map(ProductMedia::getMediaUrl)
                    .orElse(product.getMedia().get(0).getMediaUrl());
        }

        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        return new ProductSummaryDto(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getShortDescription(),
                product.getBasePrice(),
                product.getStatus(),
                primaryImageUrl,
                categoryName,
                product.getBadges()
        );
    }

    private CategoryDto mapCategoryToDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                category.getDisplayOrder(),
                category.isActive()
        );
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortBy.toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "basePrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "basePrice");
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
