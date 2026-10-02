package com.elanor.payment.provider.impl;

import com.elanor.payment.entity.PaymentProviderType;
import com.elanor.payment.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class RazorpayPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentProvider.class);

    private final String keyId;
    private final String keySecret;

    public RazorpayPaymentProvider(
            @Value("${elanor.payment.razorpay.key-id:rzp_test_key_dummy}") String keyId,
            @Value("${elanor.payment.razorpay.key-secret:rzp_test_secret_dummy}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.RAZORPAY;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentRequest request) {
        String razorpayOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("[RAZORPAY PROVIDER] Created Razorpay order [{}] for Élanor order [{}] amount ₹{}",
                razorpayOrderId, request.getOrderNumber(), request.getAmount());

        return PaymentInitiationResult.initiated(
                razorpayOrderId,
                "Razorpay order initialized. Key ID: " + keyId
        );
    }

    @Override
    public PaymentVerificationResult verify(PaymentVerificationRequest request) {
        String razorpayOrderId = request.getTransactionRef();
        String paymentId = request.getGatewayPaymentId();
        String signature = request.getGatewaySignature();

        if (paymentId == null || signature == null) {
            return PaymentVerificationResult.failed(razorpayOrderId, "Missing gateway payment id or signature.");
        }

        boolean isValid = verifySignature(razorpayOrderId, paymentId, signature, keySecret);
        if (isValid) {
            log.info("[RAZORPAY PROVIDER] Signature verified successfully for order [{}] payment [{}]", razorpayOrderId, paymentId);
            return PaymentVerificationResult.success(paymentId);
        } else {
            log.warn("[RAZORPAY PROVIDER] Signature verification failed for order [{}] payment [{}]", razorpayOrderId, paymentId);
            return PaymentVerificationResult.failed(razorpayOrderId, "Invalid payment signature verification failed.");
        }
    }

    @Override
    public RefundResult refund(RefundRequest request) {
        String rzpRefundId = "rfnd_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("[RAZORPAY PROVIDER] Dispatched refund [{}] for payment [{}] amount ₹{}",
                rzpRefundId, request.getTransactionRef(), request.getAmount());

        return RefundResult.success(rzpRefundId, request.getAmount());
    }

    public static boolean verifySignature(String orderId, String paymentId, String signature, String secret) {
        try {
            String data = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);
            return generatedSignature.equalsIgnoreCase(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error computing Razorpay HMAC signature", e);
            return false;
        }
    }

    public static boolean verifyWebhookSignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);
            return generatedSignature.equalsIgnoreCase(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error computing Webhook HMAC signature", e);
            return false;
        }
    }
}
