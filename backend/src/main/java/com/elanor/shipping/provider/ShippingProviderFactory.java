package com.elanor.shipping.provider;

import com.elanor.shipping.enums.ShipmentProviderType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ShippingProviderFactory {

    private final Map<ShipmentProviderType, ShippingProvider> providers;

    public ShippingProviderFactory(List<ShippingProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(ShippingProvider::getProviderType, Function.identity()));
    }

    public ShippingProvider getProvider(ShipmentProviderType type) {
        ShippingProvider provider = providers.get(type);
        if (provider == null) {
            return providers.get(ShipmentProviderType.MANUAL);
        }
        return provider;
    }
}
