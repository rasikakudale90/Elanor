package com.elanor.payment.provider;

import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentRequest {
    private UUID orderId;
    private String orderNumber;
    private BigDecimal amount;
    private String currency = "INR";
    private PaymentMethod paymentMethod;
    private PaymentProviderType providerType;
    private String customerEmail;
    private String customerPhone;

    public PaymentRequest() {}

    public PaymentRequest(UUID orderId, String orderNumber, BigDecimal amount, String currency,
                          PaymentMethod paymentMethod, PaymentProviderType providerType,
                          String customerEmail, String customerPhone) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.paymentMethod = paymentMethod;
        this.providerType = providerType;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(PaymentProviderType providerType) {
        this.providerType = providerType;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}
