package com.elanor.inventory.dto;

import com.elanor.inventory.entity.MovementType;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Movement type is required")
    private MovementType movementType; // RESTOCK, ADJUSTMENT, DAMAGED

    @NotNull(message = "Quantity is required")
    private int quantity;

    private String reason;
    private String referenceId;

    public StockAdjustmentRequest() {}

    public StockAdjustmentRequest(MovementType movementType, int quantity, String reason, String referenceId) {
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.referenceId = referenceId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
