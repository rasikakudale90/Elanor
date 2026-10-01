package com.elanor.payment.provider.impl;

import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CodPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(CodPaymentProvider.class);

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.COD;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentRequest request) {
        String transactionRef = "COD-TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[COD PAYMENT] Created COD payment record [{}] for order [{}] amount ₹{}",
                transactionRef, request.getOrderNumber(), request.getAmount());

        return PaymentInitiationResult.pending(
                transactionRef,
                "Cash on Delivery selected. Payment will remain PENDING until collected at delivery."
        );
    }

    @Override
    public PaymentVerificationResult verify(PaymentVerificationRequest request) {
        log.info("[COD PAYMENT] Admin verified cash collection for [{}]", request.getTransactionRef());
        return PaymentVerificationResult.success(request.getTransactionRef());
    }

    @Override
    public RefundResult refund(RefundRequest request) {
        String refundRef = "COD-REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[COD PAYMENT] Recorded manual refund requirement [{}] for COD transaction [{}]",
                refundRef, request.getTransactionRef());
        return RefundResult.success(refundRef, request.getAmount());
    }
}
