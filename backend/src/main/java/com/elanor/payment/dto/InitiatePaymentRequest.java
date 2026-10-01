package com.elanor.payment.dto;

import com.elanor.payment.entity.PaymentMethod;
import com.elanor.payment.entity.PaymentProviderType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InitiatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private PaymentMethod paymentMethod = PaymentMethod.DEMO_ONLINE;

    private PaymentProviderType provider;

    public InitiatePaymentRequest() {}

    public InitiatePaymentRequest(UUID orderId, PaymentMethod paymentMethod, PaymentProviderType provider) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.DEMO_ONLINE;
        this.provider = provider;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentProviderType getProvider() {
        return provider;
    }

    public void setProvider(PaymentProviderType provider) {
        this.provider = provider;
    }
}
