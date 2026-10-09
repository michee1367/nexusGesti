package com.nexusgesti.structure.domain;

import java.util.UUID;

public class StructureException extends RuntimeException {

    private final String code;

    public StructureException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static StructureException notFound(String type, UUID id) {
        return new StructureException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static StructureException transitionInvalide(String from, String to) {
        return new StructureException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static StructureException conflit(String code, String message) {
        return new StructureException(code, message);
    }
}
