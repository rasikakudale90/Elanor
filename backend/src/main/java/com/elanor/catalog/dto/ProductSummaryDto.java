package com.elanor.catalog.dto;

import com.elanor.catalog.entity.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSummaryDto {

    private UUID id;
    private String name;
    private String slug;
    private String shortDescription;
    private BigDecimal basePrice;
    private ProductStatus status;
    private String primaryImageUrl;
    private String categoryName;
    private String badges;

    public ProductSummaryDto() {}

    public ProductSummaryDto(UUID id, String name, String slug, String shortDescription, BigDecimal basePrice, ProductStatus status, String primaryImageUrl, String categoryName, String badges) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.basePrice = basePrice;
        this.status = status;
        this.primaryImageUrl = primaryImageUrl;
        this.categoryName = categoryName;
        this.badges = badges;
    }

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

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }
}
