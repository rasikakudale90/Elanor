package com.elanor.wishlist.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class WishlistItemDto {

    private UUID id;
    private UUID variantId;
    private String sku;
    private String productName;
    private String productSlug;
    private String variantName;
    private BigDecimal price;
    private String imageUrl;
    private boolean inStock;
    private Instant addedAt;

    public WishlistItemDto() {}

    public WishlistItemDto(UUID id, UUID variantId, String sku, String productName, String productSlug, String variantName, BigDecimal price, String imageUrl, boolean inStock, Instant addedAt) {
        this.id = id;
        this.variantId = variantId;
        this.sku = sku;
        this.productName = productName;
        this.productSlug = productSlug;
        this.variantName = variantName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
        this.addedAt = addedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
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

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
}
