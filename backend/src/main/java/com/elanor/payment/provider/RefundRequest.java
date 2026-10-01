package com.elanor.payment.provider;

import java.math.BigDecimal;
import java.util.UUID;

public class RefundRequest {
    private UUID paymentId;
    private String transactionRef;
    private BigDecimal amount;
    private String reason;

    public RefundRequest() {}

    public RefundRequest(UUID paymentId, String transactionRef, BigDecimal amount, String reason) {
        this.paymentId = paymentId;
        this.transactionRef = transactionRef;
        this.amount = amount;
        this.reason = reason;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
