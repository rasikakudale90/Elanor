package com.elanor.shipping.provider;

import com.elanor.order.entity.Order;
import com.elanor.shipping.entity.Shipment;
import com.elanor.shipping.enums.ShipmentProviderType;

public interface ShippingProvider {
    ShipmentProviderType getProviderType();
    Shipment createShipment(Order order, String carrierName, String trackingNumber, String trackingUrl);
    boolean cancelShipment(Shipment shipment);
}
