package com.elanor.shipping.dto;

import com.elanor.shipping.entity.ShipmentEvent;

import java.time.Instant;
import java.util.UUID;

public class ShipmentEventResponse {

    private UUID id;
    private String status;
    private String location;
    private String description;
    private Instant eventTime;
    private Instant createdAt;

    public ShipmentEventResponse() {}

    public ShipmentEventResponse(ShipmentEvent event) {
        if (event != null) {
            this.id = event.getId();
            this.status = event.getStatus();
            this.location = event.getLocation();
            this.description = event.getDescription();
            this.eventTime = event.getEventTime();
            this.createdAt = event.getCreatedAt();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
