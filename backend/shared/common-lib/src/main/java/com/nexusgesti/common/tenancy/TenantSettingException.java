package com.nexusgesti.common.tenancy;

public class TenantSettingException extends RuntimeException {

    public TenantSettingException(String message, Throwable cause) {
        super(message, cause);
    }
}