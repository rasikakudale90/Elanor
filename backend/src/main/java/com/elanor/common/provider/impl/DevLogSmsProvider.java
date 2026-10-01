package com.elanor.common.provider.impl;

import com.elanor.common.provider.SmsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DevLogSmsProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(DevLogSmsProvider.class);

    @Override
    public void sendOtp(String phone, String otp) {
        log.info("[DEVELOPMENT / DEMO SMS PROVIDER] Sending OTP [{}] to Phone [{}]", otp, phone);
    }
}
