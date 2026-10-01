package com.elanor.payment.dto;

import com.elanor.payment.entity.Payment;
import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentDto {
    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private PaymentProviderType paymentProvider;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String transactionRef;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;

    public PaymentDto() {}

    public PaymentDto(Payment payment) {
        if (payment != null) {
            this.id = payment.getId();
            if (payment.getOrder() != null) {
                this.orderId = payment.getOrder().getId();
                this.orderNumber = payment.getOrder().getOrderNumber();
            }
            this.paymentProvider = payment.getPaymentProvider();
            this.paymentMethod = payment.getPaymentMethod();
            this.status = payment.getStatus();
            this.amount = payment.getAmount();
            this.currency = payment.getCurrency();
            this.transactionRef = payment.getTransactionRef();
            this.errorMessage = payment.getErrorMessage();
            this.createdAt = payment.getCreatedAt();
            this.updatedAt = payment.getUpdatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public PaymentProviderType getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProviderType paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
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

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
