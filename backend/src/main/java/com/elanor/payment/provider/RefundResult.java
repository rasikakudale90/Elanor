package com.elanor.payment.provider;

import com.elanor.payment.entity.PaymentStatus;

import java.math.BigDecimal;

public class RefundResult {
    private boolean successful;
    private PaymentStatus finalStatus;
    private String refundRef;
    private BigDecimal refundedAmount;
    private String message;

    public RefundResult() {}

    public RefundResult(boolean successful, PaymentStatus finalStatus, String refundRef, BigDecimal refundedAmount, String message) {
        this.successful = successful;
        this.finalStatus = finalStatus;
        this.refundRef = refundRef;
        this.refundedAmount = refundedAmount;
        this.message = message;
    }

    public static RefundResult success(String refundRef, BigDecimal amount) {
        return new RefundResult(true, PaymentStatus.REFUNDED, refundRef, amount, "Refund processed successfully");
    }

    public static RefundResult failed(String message) {
        return new RefundResult(false, null, null, BigDecimal.ZERO, message);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public PaymentStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(PaymentStatus finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getRefundRef() {
        return refundRef;
    }

    public void setRefundRef(String refundRef) {
        this.refundRef = refundRef;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
