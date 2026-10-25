package com.nexusgesti.attendance.domain.model;

public enum StatutPresence {
    PRESENT,
    ABSENT,
    RETARD,
    ABSENT_EXCUSE,
    ABSENT_NON_EXCUSE,
    CONGE,
    MALADE,
    SORTIE_AUTORISEE;

    public boolean estPresent() {
        return this == PRESENT || this == RETARD || this == SORTIE_AUTORISEE;
    }

    public boolean estAbsent() {
        return this == ABSENT || this == ABSENT_EXCUSE || this == ABSENT_NON_EXCUSE
                || this == CONGE || this == MALADE;
    }

    public boolean estRetard() {
        return this == RETARD;
    }

    public boolean estJustifiee() {
        return this == ABSENT_EXCUSE || this == CONGE || this == MALADE;
    }
}
