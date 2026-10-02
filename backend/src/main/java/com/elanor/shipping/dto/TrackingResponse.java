package com.elanor.shipping.dto;

import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.enums.ShipmentStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrackingResponse {

    private String trackingNumber;
    private String carrierName;
    private String trackingUrl;
    private ShipmentStatus status;
    private String orderNumber;
    private Instant shippedAt;
    private Instant deliveredAt;
    private List<ShipmentEventResponse> events = new ArrayList<>();

    public TrackingResponse() {}

    public TrackingResponse(Shipment shipment) {
        if (shipment != null) {
            this.trackingNumber = shipment.getTrackingNumber();
            this.carrierName = shipment.getCarrierName();
            this.trackingUrl = shipment.getTrackingUrl();
            this.status = shipment.getStatus();
            if (shipment.getOrder() != null) {
                this.orderNumber = shipment.getOrder().getOrderNumber();
            }
            this.shippedAt = shipment.getShippedAt();
            this.deliveredAt = shipment.getDeliveredAt();
            if (shipment.getEvents() != null) {
                this.events = shipment.getEvents().stream()
                        .map(ShipmentEventResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Instant getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(Instant shippedAt) {
        this.shippedAt = shippedAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public List<ShipmentEventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<ShipmentEventResponse> events) {
        this.events = events;
    }
}
