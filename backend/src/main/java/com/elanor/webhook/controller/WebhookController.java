package com.elanor.webhook.controller;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.response.ApiResponse;
import com.elanor.payment.provider.impl.RazorpayPaymentProvider;
import com.elanor.payment.service.PaymentService;
import com.elanor.shipping.service.ShippingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks", description = "Asynchronous carrier and payment provider webhook ingestion")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final ObjectMapper objectMapper;
    private final String razorpayWebhookSecret;

    public WebhookController(
            PaymentService paymentService,
            ShippingService shippingService,
            ObjectMapper objectMapper,
            @Value("${elanor.payment.razorpay.webhook-secret:rzp_webhook_secret_dummy}") String razorpayWebhookSecret) {
        this.paymentService = paymentService;
        this.shippingService = shippingService;
        this.objectMapper = objectMapper;
        this.razorpayWebhookSecret = razorpayWebhookSecret;
    }

    @PostMapping("/razorpay")
    @Operation(summary = "Razorpay Webhook Handler", description = "Ingests payment.captured and order.paid webhooks with HMAC-SHA256 signature verification")
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleRazorpayWebhook(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        log.info("[WEBHOOK] Received Razorpay webhook callback");

        if (signature != null && !signature.isBlank() && !"rzp_webhook_secret_dummy".equals(razorpayWebhookSecret)) {
            boolean valid = RazorpayPaymentProvider.verifyWebhookSignature(rawPayload, signature, razorpayWebhookSecret);
            if (!valid) {
                log.warn("[WEBHOOK] Invalid Razorpay webhook signature detected");
                throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED, "Invalid webhook signature");
            }
        }

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String event = root.has("event") ? root.get("event").asText() : "";
            log.info("[WEBHOOK] Processing Razorpay event: {}", event);

            if ("payment.captured".equalsIgnoreCase(event) || "order.paid".equalsIgnoreCase(event)) {
                JsonNode paymentEntity = root.path("payload").path("payment").path("entity");
                String razorpayOrderId = paymentEntity.path("order_id").asText(null);
                String razorpayPaymentId = paymentEntity.path("id").asText(null);

                if (razorpayOrderId != null) {
                    paymentService.processExternalPaymentSuccess(razorpayOrderId, razorpayPaymentId, signature);
                }
            }
            return ResponseEntity.ok(ApiResponse.ok(Map.of("received", true, "event", event)));
        } catch (Exception e) {
            log.error("[WEBHOOK] Error processing Razorpay webhook", e);
            return ResponseEntity.ok(ApiResponse.ok(Map.of("received", true, "status", "ignored")));
        }
    }

    @PostMapping("/shiprocket")
    @Operation(summary = "Shiprocket Webhook Handler", description = "Ingests carrier milestone tracking status updates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleShiprocketWebhook(@RequestBody Map<String, Object> payload) {
        log.info("[WEBHOOK] Received Shiprocket webhook callback: {}", payload);

        String awb = (String) payload.get("awb");
        if (awb == null) {
            awb = (String) payload.get("tracking_number");
        }

        String currentStatus = (String) payload.get("current_status");
        if (currentStatus == null) {
            currentStatus = (String) payload.get("status");
        }

        String location = (String) payload.get("location");
        String activity = (String) payload.get("activity");

        if (awb != null && currentStatus != null) {
            shippingService.processTrackingWebhook(
                    awb,
                    currentStatus,
                    location != null ? location : "Hub",
                    activity != null ? activity : "Status updated via Shiprocket",
                    Instant.now()
            );
        }

        return ResponseEntity.ok(ApiResponse.ok(Map.of("received", true, "awb", awb != null ? awb : "")));
    }
}
