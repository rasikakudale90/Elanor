package com.elanor.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ValidateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    private BigDecimal orderAmount;

    private String guestToken;

    public ValidateCouponRequest() {}

    public ValidateCouponRequest(String code, BigDecimal orderAmount) {
        this.code = code;
        this.orderAmount = orderAmount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }
}
