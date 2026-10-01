package com.elanor.coupon.dto;

import com.elanor.coupon.entity.DiscountType;

import java.math.BigDecimal;

public class CouponValidationResponse {

    private boolean valid;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String message;

    public CouponValidationResponse() {}

    public static CouponValidationResponse invalid(String code, String message) {
        CouponValidationResponse response = new CouponValidationResponse();
        response.setValid(false);
        response.setCode(code);
        response.setDiscountAmount(BigDecimal.ZERO);
        response.setMessage(message);
        return response;
    }

    public static CouponValidationResponse valid(String code, String description, DiscountType discountType,
                                                BigDecimal discountValue, BigDecimal discountAmount,
                                                BigDecimal finalAmount, String message) {
        CouponValidationResponse response = new CouponValidationResponse();
        response.setValid(true);
        response.setCode(code);
        response.setDescription(description);
        response.setDiscountType(discountType);
        response.setDiscountValue(discountValue);
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setMessage(message);
        return response;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
