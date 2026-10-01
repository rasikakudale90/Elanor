package com.elanor.catalog.dto;

import com.elanor.catalog.entity.ProductStatus;
import com.elanor.category.dto.CategoryDto;
import com.elanor.collection.dto.CollectionDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDetailDto {

    private UUID id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private BigDecimal basePrice;
    private ProductStatus status;
    private Instant publishAt;
    private Instant unpublishAt;
    private String metaTitle;
    private String metaDescription;
    private String badges;
    private CategoryDto category;
    private List<CollectionDto> collections = new ArrayList<>();
    private List<ProductVariantDto> variants = new ArrayList<>();
    private List<ProductMediaDto> media = new ArrayList<>();

    public ProductDetailDto() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Instant getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Instant publishAt) {
        this.publishAt = publishAt;
    }

    public Instant getUnpublishAt() {
        return unpublishAt;
    }

    public void setUnpublishAt(Instant unpublishAt) {
        this.unpublishAt = unpublishAt;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public List<CollectionDto> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionDto> collections) {
        this.collections = collections;
    }

    public List<ProductVariantDto> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDto> variants) {
        this.variants = variants;
    }

    public List<ProductMediaDto> getMedia() {
        return media;
    }

    public void setMedia(List<ProductMediaDto> media) {
        this.media = media;
    }
}
