package com.elanor.order.dto;

import com.elanor.catalog.entity.ProductMedia;
import com.elanor.order.entity.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemDto {
    private UUID id;
    private UUID variantId;
    private String productName;
    private String sku;
    private String variantName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private String imageUrl;

    public OrderItemDto() {}

    public OrderItemDto(OrderItem item) {
        if (item != null) {
            this.id = item.getId();
            this.variantId = item.getVariant() != null ? item.getVariant().getId() : null;
            this.productName = item.getProductName();
            this.sku = item.getSku();
            this.variantName = item.getVariantName();
            this.unitPrice = item.getUnitPrice();
            this.quantity = item.getQuantity();
            this.totalPrice = item.getTotalPrice();
            if (item.getVariant() != null && item.getVariant().getProduct() != null &&
                    item.getVariant().getProduct().getMedia() != null) {
                this.imageUrl = item.getVariant().getProduct().getMedia().stream()
                        .filter(ProductMedia::isCover)
                        .findFirst()
                        .map(ProductMedia::getMediaUrl)
                        .orElse(null);
            }
        }
    }

    public OrderItemDto(UUID id, UUID variantId, String productName, String sku, String variantName,
                        BigDecimal unitPrice, int quantity, BigDecimal totalPrice, String imageUrl) {
        this.id = id;
        this.variantId = variantId;
        this.productName = productName;
        this.sku = sku;
        this.variantName = variantName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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
}
