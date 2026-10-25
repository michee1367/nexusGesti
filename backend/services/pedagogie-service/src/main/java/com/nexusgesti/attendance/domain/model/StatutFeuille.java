package com.nexusgesti.attendance.domain.model;

public enum StatutFeuille {
    BROUILLON,
    SOUMISE,
    VALIDEE,
    CORRIGEE,
    CLOTUREE;

    public boolean estFigee() {
        return this == VALIDEE || this == CLOTUREE;
    }
}
