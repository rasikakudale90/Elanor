package com.elanor.notification.service;

import com.elanor.notification.provider.EmailProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final EmailProvider emailProvider;

    public NotificationService(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }

    public void sendOrderConfirmation(String email, String orderNumber, String totalAmount) {
        emailProvider.sendTemplateEmail(email, "ORDER_CONFIRMED", Map.of(
                "orderNumber", orderNumber,
                "totalAmount", totalAmount
        ));
    }

    public void sendShipmentDispatched(String email, String orderNumber, String carrierName, String trackingNumber, String trackingUrl) {
        emailProvider.sendTemplateEmail(email, "SHIPMENT_DISPATCHED", Map.of(
                "orderNumber", orderNumber,
                "carrierName", carrierName != null ? carrierName : "",
                "trackingNumber", trackingNumber != null ? trackingNumber : "",
                "trackingUrl", trackingUrl != null ? trackingUrl : ""
        ));
    }

    public void sendReturnApproved(String email, String returnRequestId, String orderNumber) {
        emailProvider.sendTemplateEmail(email, "RETURN_APPROVED", Map.of(
                "returnRequestId", returnRequestId,
                "orderNumber", orderNumber
        ));
    }

    public void sendRefundProcessed(String email, String orderNumber, String refundAmount, String method) {
        emailProvider.sendTemplateEmail(email, "REFUND_PROCESSED", Map.of(
                "orderNumber", orderNumber,
                "refundAmount", refundAmount,
                "method", method
        ));
    }
}
