package com.elanor.checkout.dto;

import java.util.UUID;

public class CreateOrderRequest {

    private UUID addressId;
    private CheckoutDeliveryAddressDto deliveryAddress;
    private String couponCode;
    private String notes;
    private String paymentMethod = "DEMO_ONLINE"; // DEMO_ONLINE, COD

    public CreateOrderRequest() {}

    public CreateOrderRequest(UUID addressId, CheckoutDeliveryAddressDto deliveryAddress,
                              String couponCode, String notes, String paymentMethod) {
        this.addressId = addressId;
        this.deliveryAddress = deliveryAddress;
        this.couponCode = couponCode;
        this.notes = notes;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "DEMO_ONLINE";
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
