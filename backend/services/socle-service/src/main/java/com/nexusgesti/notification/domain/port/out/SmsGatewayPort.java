package com.nexusgesti.notification.domain.port.out;

public interface SmsGatewayPort {

    void sendSms(String phone, String message);
}
