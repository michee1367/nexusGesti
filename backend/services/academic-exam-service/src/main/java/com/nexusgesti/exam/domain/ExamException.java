package com.nexusgesti.exam.domain;

import java.util.UUID;

public class ExamException extends RuntimeException {

    private final String code;

    public ExamException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ExamException notFound(String type, UUID id) {
        return new ExamException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static ExamException transitionInvalide(String from, String to) {
        return new ExamException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static ExamException etatInvalide(String message) {
        return new ExamException("ETAT_INVALIDE", message);
    }

    public static ExamException conflit(String code, String message) {
        return new ExamException(code, message);
    }

    public static ExamException invalide(String message) {
        return new ExamException("INVALIDE", message);
    }
}
