package com.elanor.cart.dto;

import jakarta.validation.constraints.NotBlank;

public class MergeCartRequest {

    @NotBlank(message = "Guest token is required for cart merge")
    private String guestToken;

    public MergeCartRequest() {}

    public MergeCartRequest(String guestToken) {
        this.guestToken = guestToken;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }
}
