package com.elanor.common.provider;

public interface SmsProvider {
    void sendOtp(String phone, String otp);
}
