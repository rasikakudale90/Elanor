package com.elanor.notification.provider.impl;

import com.elanor.notification.provider.EmailProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Component
public class DevelopmentEmailProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentEmailProvider.class);

    // In-memory capture sink for testing and verification
    private final List<SentEmailRecord> sentEmails = new CopyOnWriteArrayList<>();

    public record SentEmailRecord(String to, String subject, String body, String templateName, Map<String, Object> variables) {}

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("""
                
                ======================== [DEV EMAIL DISPATCH] ========================
                TO: {}
                SUBJECT: {}
                BODY:
                {}
                ======================================================================
                """, to, subject, body);

        sentEmails.add(new SentEmailRecord(to, subject, body, null, Map.of()));
    }

    @Override
    public void sendTemplateEmail(String to, String templateName, Map<String, Object> variables) {
        String renderedSubject = switch (templateName) {
            case "ORDER_CONFIRMED" -> "Élanor — Order Confirmation #" + variables.getOrDefault("orderNumber", "");
            case "SHIPMENT_DISPATCHED" -> "Élanor — Your Order has Shipped (" + variables.getOrDefault("trackingNumber", "") + ")";
            case "RETURN_APPROVED" -> "Élanor — Return Request Approved";
            case "REFUND_PROCESSED" -> "Élanor — Refund Processed Successfully";
            default -> "Élanor Notification — " + templateName;
        };

        log.info("""
                
                ======================== [DEV TEMPLATE EMAIL] ========================
                TO: {}
                TEMPLATE: {}
                SUBJECT: {}
                VARIABLES: {}
                ======================================================================
                """, to, templateName, renderedSubject, variables);

        sentEmails.add(new SentEmailRecord(to, renderedSubject, null, templateName, variables));
    }

    public List<SentEmailRecord> getSentEmails() {
        return sentEmails;
    }

    public void clearSentEmails() {
        sentEmails.clear();
    }
}
