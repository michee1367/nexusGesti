package com.nexusgesti.fees.domain.model;

public enum StatutPaiement {
    EN_ATTENTE,
    VALIDE,
    REJETE,
    ANNULE,
    REMBOURSE,
    A_VERIFIER;

    public boolean estValide() {
        return this == VALIDE;
    }
}
