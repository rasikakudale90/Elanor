package com.elanor.checkout.dto;

import java.util.UUID;

public class CheckoutValidateRequest {

    private UUID addressId;
    private CheckoutDeliveryAddressDto deliveryAddress;
    private String couponCode;

    public CheckoutValidateRequest() {}

    public CheckoutValidateRequest(UUID addressId, CheckoutDeliveryAddressDto deliveryAddress, String couponCode) {
        this.addressId = addressId;
        this.deliveryAddress = deliveryAddress;
        this.couponCode = couponCode;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public CheckoutDeliveryAddressDto getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(CheckoutDeliveryAddressDto deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
