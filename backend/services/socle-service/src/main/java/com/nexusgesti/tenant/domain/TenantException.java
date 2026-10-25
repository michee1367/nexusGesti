package com.nexusgesti.tenant.domain;

import java.util.UUID;

public class TenantException extends RuntimeException {

    private final String code;

    public TenantException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static TenantException tenantAlreadyExists(UUID id) {
        return new TenantException("TENANT_ALREADY_EXISTS",
                "Un etablissement existe deja pour le tenant " + id);
    }

    public static TenantException notFound(String type, UUID id) {
        return new TenantException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static TenantException grilleFigee(UUID id) {
        return new TenantException("GRILLE_FIGEE",
                "La grille " + id + " est figee : creez une nouvelle version");
    }

    public static TenantException transitionInvalide(String from, String to) {
        return new TenantException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static TenantException conflit(String code, String message) {
        return new TenantException(code, message);
    }
}