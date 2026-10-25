package com.nexusgesti.boulangerie.approvisionnement.domain;

import java.util.UUID;

public class ApprovisionnementException extends RuntimeException {

    private final String code;

    public ApprovisionnementException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ApprovisionnementException notFound(String type, UUID id) {
        return new ApprovisionnementException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static ApprovisionnementException notFoundByCode(String type, String code) {
        return new ApprovisionnementException("NOT_FOUND", type + " introuvable : " + code);
    }

    public static ApprovisionnementException transitionInvalide(String from, String to) {
        return new ApprovisionnementException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static ApprovisionnementException etatInvalide(String message) {
        return new ApprovisionnementException("ETAT_INVALIDE", message);
    }

    public static ApprovisionnementException itemExistant(String message) {
        return new ApprovisionnementException("ITEM_EXISTANT", message);
    }

    public static ApprovisionnementException invalide(String message) {
        return new ApprovisionnementException("INVALIDE", message);
    }
}
