package com.elanor.catalog.dto;

import com.elanor.catalog.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UpdateProductRequest {

    private UUID categoryId;
    private Set<UUID> collectionIds;

    @NotBlank(message = "Product name is required")
    private String name;

    private String shortDescription;
    private String description;

    @NotNull(message = "Base price is required")
    @PositiveOrZero(message = "Base price cannot be negative")
    private BigDecimal basePrice;

    private ProductStatus status;
    private Instant publishAt;
    private Instant unpublishAt;
    private String metaTitle;
    private String metaDescription;
    private String badges;

    public UpdateProductRequest() {}

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public Set<UUID> getCollectionIds() {
        return collectionIds;
    }

    public void setCollectionIds(Set<UUID> collectionIds) {
        this.collectionIds = collectionIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
