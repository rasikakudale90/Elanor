package com.elanor.payment.provider.impl;

import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DemoPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(DemoPaymentProvider.class);

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.DEMO;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentRequest request) {
        String transactionRef = "DEMO-TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[DEMO PAYMENT] Initiated payment attempt [{}] for order [{}] amount ₹{}",
                transactionRef, request.getOrderNumber(), request.getAmount());

        return PaymentInitiationResult.initiated(
                transactionRef,
                "Demo payment initiated. Choose Demo Success or Demo Failure to complete."
        );
    }

    @Override
    public PaymentVerificationResult verify(PaymentVerificationRequest request) {
        log.info("[DEMO PAYMENT] Verifying payment [{}] with simulated outcome: {}",
                request.getTransactionRef(), request.isSimulatedSuccess() ? "SUCCESS" : "FAILURE");

        if (request.isSimulatedSuccess()) {
            return PaymentVerificationResult.success(request.getTransactionRef());
        } else {
            return PaymentVerificationResult.failed(
                    request.getTransactionRef(),
                    "Demo online payment was simulated as failed."
            );
        }
    }

    @Override
    public RefundResult refund(RefundRequest request) {
        String refundRef = "DEMO-REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[DEMO PAYMENT] Processed demo refund [{}] for payment [{}] amount ₹{}",
                refundRef, request.getTransactionRef(), request.getAmount());

        return RefundResult.success(refundRef, request.getAmount());
    }
}
