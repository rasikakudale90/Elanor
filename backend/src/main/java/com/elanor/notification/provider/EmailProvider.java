package com.elanor.notification.provider;

import java.util.Map;

public interface EmailProvider {
    void sendEmail(String to, String subject, String body);
    void sendTemplateEmail(String to, String templateName, Map<String, Object> variables);
}
