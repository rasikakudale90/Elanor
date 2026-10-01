package com.elanor.checkout.dto;

import java.math.BigDecimal;

public class ShippingQuoteResponse {

    private BigDecimal shippingCharge;
    private BigDecimal freeShippingThreshold;
    private boolean eligibleForFreeShipping;
    private BigDecimal amountNeededForFreeShipping;
    private int estimatedDeliveryMinDays;
    private int estimatedDeliveryMaxDays;
    private String estimatedDeliveryDescription;

    public ShippingQuoteResponse() {}

    public ShippingQuoteResponse(BigDecimal shippingCharge, BigDecimal freeShippingThreshold,
                                 boolean eligibleForFreeShipping, BigDecimal amountNeededForFreeShipping,
                                 int estimatedDeliveryMinDays, int estimatedDeliveryMaxDays,
                                 String estimatedDeliveryDescription) {
        this.shippingCharge = shippingCharge;
        this.freeShippingThreshold = freeShippingThreshold;
        this.eligibleForFreeShipping = eligibleForFreeShipping;
        this.amountNeededForFreeShipping = amountNeededForFreeShipping;
        this.estimatedDeliveryMinDays = estimatedDeliveryMinDays;
        this.estimatedDeliveryMaxDays = estimatedDeliveryMaxDays;
        this.estimatedDeliveryDescription = estimatedDeliveryDescription;
    }

    public BigDecimal getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(BigDecimal shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public BigDecimal getFreeShippingThreshold() {
        return freeShippingThreshold;
    }

    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) {
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public boolean isEligibleForFreeShipping() {
        return eligibleForFreeShipping;
    }

    public void setEligibleForFreeShipping(boolean eligibleForFreeShipping) {
        this.eligibleForFreeShipping = eligibleForFreeShipping;
    }

    public BigDecimal getAmountNeededForFreeShipping() {
        return amountNeededForFreeShipping;
    }

    public void setAmountNeededForFreeShipping(BigDecimal amountNeededForFreeShipping) {
        this.amountNeededForFreeShipping = amountNeededForFreeShipping;
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

    public String getEstimatedDeliveryDescription() {
        return estimatedDeliveryDescription;
    }

    public void setEstimatedDeliveryDescription(String estimatedDeliveryDescription) {
        this.estimatedDeliveryDescription = estimatedDeliveryDescription;
    }
}
