package com.nexusgesti.fees.domain.model;

public enum StatutEcheance {
    A_VENIR,
    ECHUE,
    REGLEE,
    PARTIELLEMENT_REGLEE,
    ANNULEE,
    REPORTEE;

    public boolean estReglee() {
        return this == REGLEE;
    }
}
