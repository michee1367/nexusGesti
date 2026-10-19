package com.nexusgesti.boulangerie.produit.domain;

import java.util.UUID;

public class ProduitException extends RuntimeException {

    private final String code;

    public ProduitException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ProduitException notFound(String type, UUID id) {
        return new ProduitException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static ProduitException notFoundByCode(String type, String code) {
        return new ProduitException("NOT_FOUND", type + " introuvable : " + code);
    }

    public static ProduitException transitionInvalide(String from, String to) {
        return new ProduitException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static ProduitException etatInvalide(String message) {
        return new ProduitException("ETAT_INVALIDE", message);
    }

    public static ProduitException itemExistant(String message) {
        return new ProduitException("ITEM_EXISTANT", message);
    }

    public static ProduitException invalide(String message) {
        return new ProduitException("INVALIDE", message);
    }
}
