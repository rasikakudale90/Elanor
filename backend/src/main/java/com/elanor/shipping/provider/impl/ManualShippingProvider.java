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
public class ManualShippingProvider implements ShippingProvider {

    private static final Logger log = LoggerFactory.getLogger(ManualShippingProvider.class);

    @Override
    public ShipmentProviderType getProviderType() {
        return ShipmentProviderType.MANUAL;
    }

    @Override
    public Shipment createShipment(Order order, String carrierName, String trackingNumber, String trackingUrl) {
        log.info("[MANUAL SHIPPING] Generating shipment for order [{}] via carrier [{}] with tracking [{}]",
                order.getOrderNumber(), carrierName, trackingNumber);

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setProvider(ShipmentProviderType.MANUAL);
        shipment.setCarrierName(carrierName != null ? carrierName : "BlueDart Express");
        shipment.setTrackingNumber(trackingNumber);
        shipment.setTrackingUrl(trackingUrl != null ? trackingUrl : "https://track.elanor.com/" + trackingNumber);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShippedAt(Instant.now());

        return shipment;
    }

    @Override
    public boolean cancelShipment(Shipment shipment) {
        log.info("[MANUAL SHIPPING] Cancelling shipment [{}] tracking [{}]", shipment.getId(), shipment.getTrackingNumber());
        shipment.setStatus(ShipmentStatus.CANCELLED);
        return true;
    }
}
