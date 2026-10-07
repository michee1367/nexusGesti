package com.nexusgesti.common.domain;

import java.util.UUID;

public record CorrelationId(String value) {

    public CorrelationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("correlationId is required");
        }
    }

    public static CorrelationId newOne() {
        return new CorrelationId(UUID.randomUUID().toString());
    }

    public static CorrelationId of(String value) {
        return new CorrelationId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}