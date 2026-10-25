package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum TypeArrondi {
    AUCUN,
    DECIMALE,
    INFERIEUR,
    SUPERIEUR,
    MI_SUPERIEUR;

    public BigDecimal appliquer(BigDecimal valeur, int precision) {
        if (valeur == null) {
            return null;
        }
        return switch (this) {
            case AUCUN -> valeur;
            case DECIMALE, MI_SUPERIEUR -> valeur.setScale(precision, RoundingMode.HALF_UP);
            case INFERIEUR -> valeur.setScale(precision, RoundingMode.FLOOR);
            case SUPERIEUR -> valeur.setScale(precision, RoundingMode.CEILING);
        };
    }
}