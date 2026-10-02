package com.elanor.shipping.dto;

import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.enums.ShipmentProviderType;
import com.elanor.shipping.enums.ShipmentStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShipmentResponse {

    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private ShipmentProviderType provider;
    private String carrierName;
    private String trackingNumber;
    private String trackingUrl;
    private ShipmentStatus status;
    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ShipmentEventResponse> events = new ArrayList<>();

    public ShipmentResponse() {}

    public ShipmentResponse(Shipment shipment) {
        if (shipment != null) {
            this.id = shipment.getId();
            if (shipment.getOrder() != null) {
                this.orderId = shipment.getOrder().getId();
                this.orderNumber = shipment.getOrder().getOrderNumber();
            }
            this.provider = shipment.getProvider();
            this.carrierName = shipment.getCarrierName();
            this.trackingNumber = shipment.getTrackingNumber();
            this.trackingUrl = shipment.getTrackingUrl();
            this.status = shipment.getStatus();
            this.shippedAt = shipment.getShippedAt();
            this.deliveredAt = shipment.getDeliveredAt();
            this.createdAt = shipment.getCreatedAt();
            this.updatedAt = shipment.getUpdatedAt();
            if (shipment.getEvents() != null) {
                this.events = shipment.getEvents().stream()
                        .map(ShipmentEventResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ShipmentProviderType getProvider() {
        return provider;
    }

    public void setProvider(ShipmentProviderType provider) {
        this.provider = provider;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ShipmentEventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<ShipmentEventResponse> events) {
        this.events = events;
    }
}
