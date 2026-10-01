package com.elanor.wishlist.dto;

import jakarta.validation.constraints.NotBlank;

public class MergeWishlistRequest {

    @NotBlank(message = "Guest token is required for merging")
    private String guestToken;

    public MergeWishlistRequest() {}

    public MergeWishlistRequest(String guestToken) {
        this.guestToken = guestToken;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }
}
