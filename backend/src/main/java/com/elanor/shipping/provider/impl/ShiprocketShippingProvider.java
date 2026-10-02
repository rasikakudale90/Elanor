package com.elanor.shipping.provider.impl;

import com.elanor.order.entity.Order;
import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.enums.ShipmentProviderType;
import com.elanor.shipping.enums.ShipmentStatus;
import com.elanor.shipping.provider.ShippingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ShiprocketShippingProvider implements ShippingProvider {

    private static final Logger log = LoggerFactory.getLogger(ShiprocketShippingProvider.class);

    @Override
    public ShipmentProviderType getProviderType() {
        return ShipmentProviderType.SHIPROCKET;
    }

    @Override
    public Shipment createShipment(Order order, String carrierName, String trackingNumber, String trackingUrl) {
        log.info("[SHIPROCKET ADAPTER] Delegating shipment creation for order [{}]", order.getOrderNumber());
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setProvider(ShipmentProviderType.SHIPROCKET);
        shipment.setCarrierName(carrierName != null ? carrierName : "Shiprocket Partner");
        shipment.setTrackingNumber(trackingNumber != null ? trackingNumber : "SR-" + System.currentTimeMillis());
        shipment.setTrackingUrl(trackingUrl != null ? trackingUrl : "https://shiprocket.co/tracking/" + shipment.getTrackingNumber());
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShippedAt(Instant.now());
        return shipment;
    }

    @Override
    public boolean cancelShipment(Shipment shipment) {
        log.info("[SHIPROCKET ADAPTER] Requesting AWB cancellation for tracking [{}]", shipment.getTrackingNumber());
        shipment.setStatus(ShipmentStatus.CANCELLED);
        return true;
    }
}
