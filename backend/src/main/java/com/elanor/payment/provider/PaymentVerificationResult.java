package com.elanor.payment.provider;

import com.elanor.payment.entity.PaymentStatus;

public class PaymentVerificationResult {
    private boolean successful;
    private PaymentStatus finalStatus;
    private String transactionRef;
    private String errorMessage;

    public PaymentVerificationResult() {}

    public PaymentVerificationResult(boolean successful, PaymentStatus finalStatus, String transactionRef, String errorMessage) {
        this.successful = successful;
        this.finalStatus = finalStatus;
        this.transactionRef = transactionRef;
        this.errorMessage = errorMessage;
    }

    public static PaymentVerificationResult success(String transactionRef) {
        return new PaymentVerificationResult(true, PaymentStatus.SUCCESSFUL, transactionRef, null);
    }

    public static PaymentVerificationResult failed(String transactionRef, String errorMessage) {
        return new PaymentVerificationResult(false, PaymentStatus.FAILED, transactionRef, errorMessage);
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
}
