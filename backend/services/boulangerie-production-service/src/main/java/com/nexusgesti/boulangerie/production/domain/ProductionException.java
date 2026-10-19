package com.nexusgesti.boulangerie.production.domain;

import java.util.UUID;

public class ProductionException extends RuntimeException {

    private final String code;

    public ProductionException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ProductionException notFound(String type, UUID id) {
        return new ProductionException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static ProductionException notFoundByCode(String type, String code) {
        return new ProductionException("NOT_FOUND", type + " introuvable : " + code);
    }

    public static ProductionException transitionInvalide(String from, String to) {
        return new ProductionException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static ProductionException etatInvalide(String message) {
        return new ProductionException("ETAT_INVALIDE", message);
    }

    public static ProductionException itemExistant(String message) {
        return new ProductionException("ITEM_EXISTANT", message);
    }

    public static ProductionException invalide(String message) {
        return new ProductionException("INVALIDE", message);
    }
}
