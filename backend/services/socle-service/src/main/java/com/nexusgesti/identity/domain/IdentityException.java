package com.nexusgesti.identity.domain;

import java.util.UUID;

public class IdentityException extends RuntimeException {

    private final String code;

    public IdentityException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static IdentityException notFound(String type, UUID id) {
        return new IdentityException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static IdentityException utilisateurDejaExistant(String email) {
        return new IdentityException("UTILISATEUR_EXISTANT",
                "Un utilisateur existe deja pour " + email);
    }

    public static IdentityException transitionInvalide(String from, String to) {
        return new IdentityException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static IdentityException keycloakIndisponible(String detail) {
        return new IdentityException("KEYCLOAK_INDISPONIBLE", detail);
    }

    public static IdentityException conflit(String code, String message) {
        return new IdentityException(code, message);
    }
}
