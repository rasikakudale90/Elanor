package com.elanor.catalog.service;

import com.elanor.catalog.dto.*;
import com.elanor.catalog.entity.*;
import com.elanor.catalog.repository.*;
import com.elanor.category.dto.CategoryDto;
import com.elanor.category.entity.Category;
import com.elanor.category.repository.CategoryRepository;
import com.elanor.collection.dto.CollectionDto;
import com.elanor.collection.entity.Collection;
import com.elanor.collection.repository.CollectionRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMediaRepository productMediaRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;

    public CatalogService(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductMediaRepository productMediaRepository,
            CategoryRepository categoryRepository,
            CollectionRepository collectionRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMediaRepository = productMediaRepository;
        this.categoryRepository = categoryRepository;
        this.collectionRepository = collectionRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDto> getPublicProducts(UUID categoryId, String collectionSlug, Pageable pageable) {
        Instant now = Instant.now();
        Page<Product> page;
        if (collectionSlug != null && !collectionSlug.isBlank()) {
            page = productRepository.findPublicByCollectionSlug(collectionSlug, now, pageable);
        } else {
            page = productRepository.findPublicProducts(categoryId, now, pageable);
        }
        return page.map(this::mapToSummaryDto);
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getPublicProductBySlug(String slug) {
        Product product = productRepository.findPublicBySlug(slug, Instant.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found or currently unavailable."));

        return mapToDetailDto(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getAdminProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found with id: " + id));

        return mapToDetailDto(product);
    }

    @Transactional
    public ProductDetailDto createProduct(CreateProductRequest request) {
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? generateSlug(request.getSlug())
                : generateSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Product with slug '" + slug + "' already exists.");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found."));
        }

        Set<Collection> collections = new HashSet<>();
        if (request.getCollectionIds() != null && !request.getCollectionIds().isEmpty()) {
            collections.addAll(collectionRepository.findAllById(request.getCollectionIds()));
        }

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setSlug(slug);
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT);
        product.setPublishAt(request.getPublishAt());
        product.setUnpublishAt(request.getUnpublishAt());
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setBadges(request.getBadges());
        product.setCategory(category);
        product.setCollections(collections);

        Product saved = productRepository.save(product);

        // Auto-create standard primary variant if requested base price is set
        String defaultSku = "SKU-" + slug.toUpperCase();
        if (!productVariantRepository.existsBySku(defaultSku)) {
            ProductVariant defaultVariant = new ProductVariant(
                    saved,
                    defaultSku,
                    saved.getName() + " Standard",
                    saved.getBasePrice(),
                    null,
                    "{\"type\":\"standard\"}"
            );
            productVariantRepository.save(defaultVariant);
            saved.getVariants().add(defaultVariant);
        }

        return mapToDetailDto(saved);
    }

    @Transactional
    public ProductDetailDto updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found."));
        }

        Set<Collection> collections = new HashSet<>();
        if (request.getCollectionIds() != null) {
            collections.addAll(collectionRepository.findAllById(request.getCollectionIds()));
            product.setCollections(collections);
        }

        product.setName(request.getName().trim());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        product.setPublishAt(request.getPublishAt());
        product.setUnpublishAt(request.getUnpublishAt());
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setBadges(request.getBadges());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return mapToDetailDto(saved);
    }

    @Transactional
    public void updateProductStatus(UUID id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));
        product.setStatus(status);
        productRepository.save(product);
    }

    @Transactional
    public ProductVariantDto createVariant(UUID productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        String sku = request.getSku().trim().toUpperCase();
        if (productVariantRepository.existsBySku(sku)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Variant with SKU '" + sku + "' already exists.");
        }

        ProductVariant variant = new ProductVariant(
                product,
                sku,
                request.getName(),
                request.getPrice(),
                request.getCompareAtPrice(),
                request.getAttributesJson()
        );
        variant.setActive(request.isActive());

        ProductVariant saved = productVariantRepository.save(variant);
        return mapToVariantDto(saved);
    }

    @Transactional
    public ProductVariantDto updateVariant(UUID productId, UUID variantId, UpdateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        ProductVariant variant = productVariantRepository.findByIdAndProduct(variantId, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND, "Variant not found for this product."));

        variant.setName(request.getName());
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getAttributesJson() != null) {
            variant.setAttributesJson(request.getAttributesJson());
        }
        variant.setActive(request.isActive());

        ProductVariant saved = productVariantRepository.save(variant);
        return mapToVariantDto(saved);
    }

    @Transactional
    public void deleteVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        ProductVariant variant = productVariantRepository.findByIdAndProduct(variantId, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND, "Variant not found for this product."));

        productVariantRepository.delete(variant);
    }

    @Transactional
    public ProductMediaDto addMedia(UUID productId, CreateMediaRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findByIdAndProduct(request.getVariantId(), product)
                    .orElse(null);
        }

        ProductMedia media = new ProductMedia(
                product,
                variant,
                request.getMediaUrl(),
                request.getMediaType(),
                request.getDisplayOrder(),
                request.isCover()
        );

        ProductMedia saved = productMediaRepository.save(media);
        return mapToMediaDto(saved);
    }

    @Transactional
    public void deleteMedia(UUID productId, UUID mediaId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found."));

        ProductMedia media = productMediaRepository.findByIdAndProduct(mediaId, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Media asset not found."));

        productMediaRepository.delete(media);
    }

    private ProductSummaryDto mapToSummaryDto(Product product) {
        String primaryImage = product.getMedia().stream()
                .filter(ProductMedia::isCover)
                .findFirst()
                .map(ProductMedia::getMediaUrl)
                .orElseGet(() -> product.getMedia().isEmpty() ? null : product.getMedia().get(0).getMediaUrl());

        return new ProductSummaryDto(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getShortDescription(),
                product.getBasePrice(),
                product.getStatus(),
                primaryImage,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getBadges()
        );
    }

    private ProductDetailDto mapToDetailDto(Product product) {
        ProductDetailDto dto = new ProductDetailDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setShortDescription(product.getShortDescription());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setStatus(product.getStatus());
        dto.setPublishAt(product.getPublishAt());
        dto.setUnpublishAt(product.getUnpublishAt());
        dto.setMetaTitle(product.getMetaTitle());
        dto.setMetaDescription(product.getMetaDescription());
        dto.setBadges(product.getBadges());

        if (product.getCategory() != null) {
            dto.setCategory(new CategoryDto(
                    product.getCategory().getId(),
                    product.getCategory().getParent() != null ? product.getCategory().getParent().getId() : null,
                    product.getCategory().getName(),
                    product.getCategory().getSlug(),
                    product.getCategory().getDescription(),
                    product.getCategory().getImageUrl(),
                    product.getCategory().getDisplayOrder(),
                    product.getCategory().isActive()
            ));
        }

        if (product.getCollections() != null) {
            dto.setCollections(product.getCollections().stream()
                    .map(c -> new CollectionDto(c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getBannerUrl(), c.isActive()))
                    .collect(Collectors.toList()));
        }

        if (product.getVariants() != null) {
            dto.setVariants(product.getVariants().stream()
                    .map(this::mapToVariantDto)
                    .collect(Collectors.toList()));
        }

        if (product.getMedia() != null) {
            dto.setMedia(product.getMedia().stream()
                    .map(this::mapToMediaDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ProductVariantDto mapToVariantDto(ProductVariant variant) {
        return new ProductVariantDto(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getSku(),
                variant.getName(),
                variant.getPrice(),
                variant.getCompareAtPrice(),
                variant.getAttributesJson(),
                variant.isActive()
        );
    }

    private ProductMediaDto mapToMediaDto(ProductMedia media) {
        return new ProductMediaDto(
                media.getId(),
                media.getProduct().getId(),
                media.getVariant() != null ? media.getVariant().getId() : null,
                media.getMediaUrl(),
                media.getMediaType(),
                media.getDisplayOrder(),
                media.isCover()
        );
    }

    private String generateSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
