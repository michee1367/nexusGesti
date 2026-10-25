package com.nexusgesti.fees.domain.model;

public enum StatutCoupon {
    A_TIRER,
    TIRE,
    REMIS,
    ARCHIVE,
    ANNULE;

    public boolean estAnnule() {
        return this == ANNULE;
    }
}
