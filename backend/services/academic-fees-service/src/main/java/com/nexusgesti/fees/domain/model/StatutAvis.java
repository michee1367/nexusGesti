package com.nexusgesti.fees.domain.model;

public enum StatutAvis {
    BROUILLON,
    EMIS,
    PARTIELLEMENT_REGLE,
    REGLE,
    ANNULE,
    EN_RETARD,
    TRANSFERE;

    public boolean estRegle() {
        return this == REGLE;
    }

    public boolean estActif() {
        return this == EMIS || this == PARTIELLEMENT_REGLE || this == EN_RETARD;
    }
}
