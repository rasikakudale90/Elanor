package com.elanor.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CatalogContextDto {

    private UUID productId;
    private UUID variantId;
    private String name;
    private String subtitle;
    private String slug;
    private String description;
    private String categoryName;
    private BigDecimal price;
    private String imageUrl;
    private boolean inStock;

    public CatalogContextDto() {}

    public CatalogContextDto(UUID productId, UUID variantId, String name, String subtitle, String slug, String description, String categoryName, BigDecimal price, String imageUrl, boolean inStock) {
        this.productId = productId;
        this.variantId = variantId;
        this.name = name;
        this.subtitle = subtitle;
        this.slug = slug;
        this.description = description;
        this.categoryName = categoryName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
