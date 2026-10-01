package com.elanor.checkout.dto;

import java.math.BigDecimal;

public class ShippingQuoteRequest {

    private BigDecimal subtotal;
    private String postalCode;
    private String state;
    private String city;

    public ShippingQuoteRequest() {}

    public ShippingQuoteRequest(BigDecimal subtotal, String postalCode, String state, String city) {
        this.subtotal = subtotal;
        this.postalCode = postalCode;
        this.state = state;
        this.city = city;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
