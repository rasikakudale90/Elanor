package com.elanor.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AddToCartRequest {

    @NotNull(message = "Product variant ID is required")
    private UUID variantId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    public AddToCartRequest() {}

    public AddToCartRequest(UUID variantId, int quantity) {
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
