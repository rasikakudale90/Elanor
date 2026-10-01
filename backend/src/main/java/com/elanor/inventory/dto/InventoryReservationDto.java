package com.elanor.inventory.dto;

import com.elanor.inventory.entity.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public class InventoryReservationDto {

    private UUID id;
    private UUID variantId;
    private String sku;
    private UUID orderId;
    private int quantity;
    private ReservationStatus status;
    private Instant expiresAt;
    private Instant createdAt;

    public InventoryReservationDto() {}

    public InventoryReservationDto(UUID id, UUID variantId, String sku, UUID orderId, int quantity, ReservationStatus status, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.variantId = variantId;
        this.sku = sku;
        this.orderId = orderId;
        this.quantity = quantity;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
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

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
