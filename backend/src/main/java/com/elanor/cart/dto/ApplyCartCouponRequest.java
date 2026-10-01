package com.elanor.cart.dto;

import jakarta.validation.constraints.NotBlank;

public class ApplyCartCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    public ApplyCartCouponRequest() {}

    public ApplyCartCouponRequest(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
