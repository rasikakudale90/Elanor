package com.elanor.returns.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateReturnItemDto {

    @NotNull(message = "Order item ID is required")
    private UUID orderItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    private String conditionNote;

    public CreateReturnItemDto() {}

    public CreateReturnItemDto(UUID orderItemId, int quantity, String conditionNote) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.conditionNote = conditionNote;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }
}
