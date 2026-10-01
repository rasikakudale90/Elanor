package com.elanor.inventory.dto;

import java.time.Instant;
import java.util.UUID;

public class InventoryDto {

    private UUID id;
    private UUID variantId;
    private String sku;
    private String productName;
    private String variantName;
    private int onHand;
    private int reserved;
    private int available;
    private int lowStockThreshold;
    private boolean lowStock;
    private Instant updatedAt;

    public InventoryDto() {}

    public InventoryDto(UUID id, UUID variantId, String sku, String productName, String variantName, int onHand, int reserved, int available, int lowStockThreshold, boolean lowStock, Instant updatedAt) {
        this.id = id;
        this.variantId = variantId;
        this.sku = sku;
        this.productName = productName;
        this.variantName = variantName;
        this.onHand = onHand;
        this.reserved = reserved;
        this.available = available;
        this.lowStockThreshold = lowStockThreshold;
        this.lowStock = lowStock;
        this.updatedAt = updatedAt;
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

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
