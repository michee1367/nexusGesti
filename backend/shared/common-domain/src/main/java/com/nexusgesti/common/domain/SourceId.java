package com.nexusgesti.common.domain;

import java.util.UUID;

public record SourceId(String service, UUID id) {

    public SourceId {
        if (service == null || service.isBlank()) {
            throw new IllegalArgumentException("sourceId.service is required");
        }
        if (id == null) {
            throw new IllegalArgumentException("sourceId.id is required");
        }
    }

    public static SourceId of(String service, UUID id) {
        return new SourceId(service, id);
    }

    public String compositeWith(CodeActe codeActe) {
        if (codeActe == null) {
            throw new IllegalArgumentException("codeActe is required");
        }
        return service + ":" + id + ":" + codeActe.code();
    }

    @Override
    public String toString() {
        return service + ":" + id;
    }
}