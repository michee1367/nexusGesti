package com.nexusgesti.fees.domain.model;

public enum Periodicite {
    PONCTUELLE,
    MENSUELLE,
    TRIMESTRIELLE,
    SEMESTRIELLE,
    ANNUELLE;

    public boolean estPeriodique() {
        return this != PONCTUELLE;
    }
}
