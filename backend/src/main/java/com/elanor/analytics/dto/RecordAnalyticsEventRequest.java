package com.elanor.analytics.dto;

import jakarta.validation.constraints.NotBlank;

public class RecordAnalyticsEventRequest {

    @NotBlank(message = "Event type is required")
    private String eventType;

    private String guestToken;

    private String sessionId;

    private String metadataJson;

    public RecordAnalyticsEventRequest() {}

    public RecordAnalyticsEventRequest(String eventType, String guestToken, String sessionId, String metadataJson) {
        this.eventType = eventType;
        this.guestToken = guestToken;
        this.sessionId = sessionId;
        this.metadataJson = metadataJson;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
