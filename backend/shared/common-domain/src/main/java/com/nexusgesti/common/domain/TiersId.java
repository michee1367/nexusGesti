package com.nexusgesti.common.domain;

import java.util.UUID;

public record TiersId(UUID value) {

    public TiersId {
        if (value == null) {
            throw new IllegalArgumentException("tiersId is required");
        }
    }

    public static TiersId of(UUID value) {
        return new TiersId(value);
    }

    public UUID asUuid() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}