package com.elanor.shipping.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class AddShipmentEventRequest {

    @NotBlank(message = "Event status is required")
    private String status;

    private String location;

    private String description;

    private Instant eventTime;

    public AddShipmentEventRequest() {}

    public AddShipmentEventRequest(String status, String location, String description, Instant eventTime) {
        this.status = status;
        this.location = location;
        this.description = description;
        this.eventTime = eventTime != null ? eventTime : Instant.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
}
