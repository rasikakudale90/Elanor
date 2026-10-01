package com.elanor.inventory.dto;

import com.elanor.inventory.entity.MovementType;

import java.time.Instant;
import java.util.UUID;

public class InventoryMovementDto {

    private UUID id;
    private UUID variantId;
    private String sku;
    private MovementType movementType;
    private int quantity;
    private String referenceId;
    private String reason;
    private String actor;
    private Instant createdAt;

    public InventoryMovementDto() {}

    public InventoryMovementDto(UUID id, UUID variantId, String sku, MovementType movementType, int quantity, String referenceId, String reason, String actor, Instant createdAt) {
        this.id = id;
        this.variantId = variantId;
        this.sku = sku;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.reason = reason;
        this.actor = actor;
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

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
