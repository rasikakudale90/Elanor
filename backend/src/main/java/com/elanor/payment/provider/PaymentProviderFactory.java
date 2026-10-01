package com.elanor.payment.provider;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.payment.entity.PaymentProviderType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentProviderFactory {

    private final Map<PaymentProviderType, PaymentProvider> providers;

    public PaymentProviderFactory(List<PaymentProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(PaymentProvider::getProviderType, Function.identity()));
    }

    public PaymentProvider getProvider(PaymentProviderType providerType) {
        PaymentProvider provider = providers.get(providerType);
        if (provider == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Unsupported payment provider: " + providerType);
        }
        return provider;
    }
}
