package com.elanor.cart.dto;

import jakarta.validation.constraints.Min;

public class UpdateCartItemRequest {

    @Min(value = 0, message = "Quantity must be at least 0 (0 to remove)")
    private int quantity;

    public UpdateCartItemRequest() {}

    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
