package com.nexusgesti.enrollment.domain;

import java.util.UUID;

public class EnrollmentException extends RuntimeException {

    private final String code;

    public EnrollmentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EnrollmentException notFound(String type, UUID id) {
        return new EnrollmentException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static EnrollmentException transitionInvalide(String from, String to) {
        return new EnrollmentException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static EnrollmentException conflit(String code, String message) {
        return new EnrollmentException(code, message);
    }

    public static EnrollmentException etatInvalide(String message) {
        return new EnrollmentException("ETAT_INVALIDE", message);
    }

    public static EnrollmentException invalide(String message) {
        return new EnrollmentException("INVALIDE", message);
    }
}
