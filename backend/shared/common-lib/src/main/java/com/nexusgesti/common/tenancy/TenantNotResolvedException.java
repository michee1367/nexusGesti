package com.nexusgesti.common.tenancy;

public class TenantNotResolvedException extends RuntimeException {

    public TenantNotResolvedException(String message) {
        super(message);
    }
}