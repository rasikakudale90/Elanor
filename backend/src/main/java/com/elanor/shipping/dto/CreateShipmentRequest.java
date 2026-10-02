package com.elanor.shipping.dto;

import com.elanor.shipping.enums.ShipmentProviderType;
import com.elanor.shipping.enums.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateShipmentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private ShipmentProviderType provider = ShipmentProviderType.MANUAL;

    @NotBlank(message = "Carrier name is required")
    private String carrierName;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    private String trackingUrl;

    private ShipmentStatus status = ShipmentStatus.IN_TRANSIT;

    private String initialLocation;

    private String initialDescription;

    public CreateShipmentRequest() {}

    public CreateShipmentRequest(UUID orderId, ShipmentProviderType provider, String carrierName,
                                 String trackingNumber, String trackingUrl, ShipmentStatus status,
                                 String initialLocation, String initialDescription) {
        this.orderId = orderId;
        this.provider = provider;
        this.carrierName = carrierName;
        this.trackingNumber = trackingNumber;
        this.trackingUrl = trackingUrl;
        this.status = status;
        this.initialLocation = initialLocation;
        this.initialDescription = initialDescription;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
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

    public String getInitialLocation() {
        return initialLocation;
    }

    public void setInitialLocation(String initialLocation) {
        this.initialLocation = initialLocation;
    }

    public String getInitialDescription() {
        return initialDescription;
    }

    public void setInitialDescription(String initialDescription) {
        this.initialDescription = initialDescription;
    }
}
