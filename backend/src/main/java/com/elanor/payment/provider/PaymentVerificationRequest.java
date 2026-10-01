package com.elanor.payment.provider;

import java.util.Map;
import java.util.UUID;

public class PaymentVerificationRequest {
    private UUID paymentId;
    private String transactionRef;
    private boolean simulatedSuccess = true;
    private Map<String, Object> providerPayload;

    public PaymentVerificationRequest() {}

    public PaymentVerificationRequest(UUID paymentId, String transactionRef, boolean simulatedSuccess, Map<String, Object> providerPayload) {
        this.paymentId = paymentId;
        this.transactionRef = transactionRef;
        this.simulatedSuccess = simulatedSuccess;
        this.providerPayload = providerPayload;
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

    public boolean isSimulatedSuccess() {
        return simulatedSuccess;
    }

    public void setSimulatedSuccess(boolean simulatedSuccess) {
        this.simulatedSuccess = simulatedSuccess;
    }

    public Map<String, Object> getProviderPayload() {
        return providerPayload;
    }

    public void setProviderPayload(Map<String, Object> providerPayload) {
        this.providerPayload = providerPayload;
    }
}
