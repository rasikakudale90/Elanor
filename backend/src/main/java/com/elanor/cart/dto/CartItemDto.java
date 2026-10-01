package com.elanor.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemDto {

    private UUID id;
    private UUID variantId;
    private String sku;
    private String productName;
    private String productSlug;
    private String variantName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private String imageUrl;
    private int availableStock;

    public CartItemDto() {}

    public CartItemDto(UUID id, UUID variantId, String sku, String productName, String productSlug, String variantName, BigDecimal unitPrice, int quantity, BigDecimal totalPrice, String imageUrl, int availableStock) {
        this.id = id;
        this.variantId = variantId;
        this.sku = sku;
        this.productName = productName;
        this.productSlug = productSlug;
        this.variantName = variantName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
        this.availableStock = availableStock;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }
}
