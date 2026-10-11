package com.nexusgesti.fees.domain;

import java.util.UUID;

public class FeesException extends RuntimeException {

    private final String code;

    public FeesException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static FeesException notFound(String type, UUID id) {
        return new FeesException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static FeesException transitionInvalide(String from, String to) {
        return new FeesException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static FeesException etatInvalide(String message) {
        return new FeesException("ETAT_INVALIDE", message);
    }

    public static FeesException conflit(String code, String message) {
        return new FeesException(code, message);
    }

    public static FeesException invalide(String message) {
        return new FeesException("INVALIDE", message);
    }
}
