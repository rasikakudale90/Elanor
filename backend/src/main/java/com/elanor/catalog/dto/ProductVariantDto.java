package com.elanor.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductVariantDto {

    private UUID id;
    private UUID productId;
    private String sku;
    private String name;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private String attributesJson;
    private boolean active;

    public ProductVariantDto() {}

    public ProductVariantDto(UUID id, UUID productId, String sku, String name, BigDecimal price, BigDecimal compareAtPrice, String attributesJson, boolean active) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.compareAtPrice = compareAtPrice;
        this.attributesJson = attributesJson;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public String getAttributesJson() {
        return attributesJson;
    }

    public void setAttributesJson(String attributesJson) {
        this.attributesJson = attributesJson;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
