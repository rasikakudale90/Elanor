package com.elanor.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AddWishlistItemRequest {

    @NotNull(message = "Product variant ID is required")
    private UUID variantId;

    public AddWishlistItemRequest() {}

    public AddWishlistItemRequest(UUID variantId) {
        this.variantId = variantId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }
}
