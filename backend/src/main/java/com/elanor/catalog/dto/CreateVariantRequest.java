package com.elanor.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class CreateVariantRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    private String name;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private BigDecimal price;

    private BigDecimal compareAtPrice;
    private String attributesJson = "{}";
    private boolean active = true;

    public CreateVariantRequest() {}

    public CreateVariantRequest(String sku, String name, BigDecimal price, BigDecimal compareAtPrice, String attributesJson, boolean active) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.compareAtPrice = compareAtPrice;
        this.attributesJson = attributesJson != null ? attributesJson : "{}";
        this.active = active;
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
