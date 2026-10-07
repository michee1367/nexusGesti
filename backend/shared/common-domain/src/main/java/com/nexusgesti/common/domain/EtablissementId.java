package com.nexusgesti.common.domain;

import java.util.UUID;

public record EtablissementId(UUID value) {

    public EtablissementId {
        if (value == null) {
            throw new IllegalArgumentException("etablissementId is required");
        }
    }

    public static EtablissementId of(UUID value) {
        return new EtablissementId(value);
    }

    public static EtablissementId parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("etablissementId is required");
        }
        return new EtablissementId(UUID.fromString(value));
    }

    public UUID asUuid() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}