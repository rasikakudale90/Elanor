package com.elanor.payment.provider;

import com.elanor.payment.entity.PaymentStatus;

public class PaymentInitiationResult {
    private boolean success;
    private String transactionRef;
    private PaymentStatus initialStatus;
    private String redirectUrl;
    private String message;

    public PaymentInitiationResult() {}

    public PaymentInitiationResult(boolean success, String transactionRef, PaymentStatus initialStatus, String redirectUrl, String message) {
        this.success = success;
        this.transactionRef = transactionRef;
        this.initialStatus = initialStatus;
        this.redirectUrl = redirectUrl;
        this.message = message;
    }

    public static PaymentInitiationResult initiated(String transactionRef, String message) {
        return new PaymentInitiationResult(true, transactionRef, PaymentStatus.INITIATED, null, message);
    }

    public static PaymentInitiationResult pending(String transactionRef, String message) {
        return new PaymentInitiationResult(true, transactionRef, PaymentStatus.PENDING, null, message);
    }

    public static PaymentInitiationResult failed(String message) {
        return new PaymentInitiationResult(false, null, PaymentStatus.FAILED, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public PaymentStatus getInitialStatus() {
        return initialStatus;
    }

    public void setInitialStatus(PaymentStatus initialStatus) {
        this.initialStatus = initialStatus;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
