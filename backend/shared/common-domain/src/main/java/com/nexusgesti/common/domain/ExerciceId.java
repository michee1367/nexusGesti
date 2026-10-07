package com.nexusgesti.common.domain;

import java.util.UUID;

public record ExerciceId(UUID value) {

    public ExerciceId {
        if (value == null) {
            throw new IllegalArgumentException("exerciceId is required");
        }
    }

    public static ExerciceId of(UUID value) {
        return new ExerciceId(value);
    }

    public UUID asUuid() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}