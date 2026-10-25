package com.nexusgesti.attendance.domain;

import java.util.UUID;

public class AttendanceException extends RuntimeException {

    private final String code;

    public AttendanceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AttendanceException notFound(String type, UUID id) {
        return new AttendanceException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static AttendanceException transitionInvalide(String from, String to) {
        return new AttendanceException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static AttendanceException conflit(String code, String message) {
        return new AttendanceException(code, message);
    }

    public static AttendanceException etatInvalide(String message) {
        return new AttendanceException("ETAT_INVALIDE", message);
    }

    public static AttendanceException invalide(String message) {
        return new AttendanceException("INVALIDE", message);
    }
}
