package com.elanor.checkout.dto;

import com.elanor.cart.dto.CartItemDto;
import com.elanor.order.dto.OrderAddressDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckoutValidateResponse {

    private boolean valid;
    private List<String> errors = new ArrayList<>();
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal shippingCharge = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String appliedCouponCode;
    private boolean eligibleForFreeShipping;
    private int estimatedDeliveryMinDays;
    private int estimatedDeliveryMaxDays;
    private OrderAddressDto validatedAddress;

    public CheckoutValidateResponse() {}

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(BigDecimal shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public void setAppliedCouponCode(String appliedCouponCode) {
        this.appliedCouponCode = appliedCouponCode;
    }

    public boolean isEligibleForFreeShipping() {
        return eligibleForFreeShipping;
    }

    public void setEligibleForFreeShipping(boolean eligibleForFreeShipping) {
        this.eligibleForFreeShipping = eligibleForFreeShipping;
    }

    public int getEstimatedDeliveryMinDays() {
        return estimatedDeliveryMinDays;
    }

    public void setEstimatedDeliveryMinDays(int estimatedDeliveryMinDays) {
        this.estimatedDeliveryMinDays = estimatedDeliveryMinDays;
    }

    public int getEstimatedDeliveryMaxDays() {
        return estimatedDeliveryMaxDays;
    }

    public void setEstimatedDeliveryMaxDays(int estimatedDeliveryMaxDays) {
        this.estimatedDeliveryMaxDays = estimatedDeliveryMaxDays;
    }

    public OrderAddressDto getValidatedAddress() {
        return validatedAddress;
    }

    public void setValidatedAddress(OrderAddressDto validatedAddress) {
        this.validatedAddress = validatedAddress;
    }
}
