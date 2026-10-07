package com.nexusgesti.common.domain;

import java.util.UUID;

public record PatientId(UUID value) {

    public PatientId {
        if (value == null) {
            throw new IllegalArgumentException("patientId is required");
        }
    }

    public static PatientId of(UUID value) {
        return new PatientId(value);
    }

    public UUID asUuid() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}