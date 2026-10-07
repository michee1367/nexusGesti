package com.nexusgesti.common.tenancy;

import com.nexusgesti.common.domain.EtablissementId;
import com.nexusgesti.common.domain.TenantHeaders;

import java.util.Optional;

public final class TenantContext {

    public static final String HEADER = TenantHeaders.HEADER;

    private static final ThreadLocal<EtablissementId> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(EtablissementId etablissementId) {
        if (etablissementId == null) {
            throw new IllegalArgumentException("etablissementId is required");
        }
        CURRENT.set(etablissementId);
    }

    public static Optional<EtablissementId> get() {
        return Optional.ofNullable(CURRENT.get());
    }

    /**
     * Pose le tenant lu dans un evenement Kafka. Renvoie false si l'absence ou
     * la forme de l'identifiant empeche de le resoudre : l'ecriture qui suit
     * sera alors refusee par la policy RLS (fail closed).
     */
    public static boolean setIfPresent(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return false;
        }
        try {
            CURRENT.set(EtablissementId.parse(tenantId));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static EtablissementId require() {
        EtablissementId etablissementId = CURRENT.get();
        if (etablissementId == null) {
            throw new TenantNotResolvedException(
                    "No etablissement in context. Missing " + HEADER + " header?");
        }
        return etablissementId;
    }

    public static void clear() {
        CURRENT.remove();
    }
}