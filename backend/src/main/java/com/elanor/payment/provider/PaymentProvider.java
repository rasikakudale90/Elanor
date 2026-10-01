package com.elanor.payment.provider;

import com.elanor.payment.entity.PaymentProviderType;

public interface PaymentProvider {

    PaymentProviderType getProviderType();

    PaymentInitiationResult initiate(PaymentRequest request);

    PaymentVerificationResult verify(PaymentVerificationRequest request);

    RefundResult refund(RefundRequest request);
}
