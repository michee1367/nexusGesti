package com.nexusgesti.boulangerie.vente.domain;

import java.util.UUID;

public class VenteException extends RuntimeException {

    private final String code;

    public VenteException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VenteException notFound(String type, UUID id) {
        return new VenteException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static VenteException transitionInvalide(String from, String to) {
        return new VenteException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static VenteException etatInvalide(String message) {
        return new VenteException("ETAT_INVALIDE", message);
    }

    public static VenteException idempotenceEnCours(String message) {
        return new VenteException("IDEMPOTENCE_EN_COURS", message);
    }

    public static VenteException itemExistant(String message) {
        return new VenteException("ITEM_EXISTANT", message);
    }

    public static VenteException invalide(String message) {
        return new VenteException("INVALIDE", message);
    }
}
