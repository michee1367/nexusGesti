package com.nexusgesti.assessment.domain;

import java.util.UUID;

public class AssessmentException extends RuntimeException {

    private final String code;

    public AssessmentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AssessmentException notFound(String type, UUID id) {
        return new AssessmentException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static AssessmentException transitionInvalide(String from, String to) {
        return new AssessmentException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static AssessmentException etatInvalide(String message) {
        return new AssessmentException("ETAT_INVALIDE", message);
    }

    public static AssessmentException conflit(String code, String message) {
        return new AssessmentException(code, message);
    }

    public static AssessmentException invalide(String message) {
        return new AssessmentException("INVALIDE", message);
    }
}
