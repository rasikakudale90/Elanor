package com.elanor.payment.dto;

public class DemoPaymentResultRequest {

    private boolean success = true;

    public DemoPaymentResultRequest() {}

    public DemoPaymentResultRequest(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
