package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record BesoinApprovisionnement(String articleCode,
                                      String libelle,
                                      BigDecimal quantiteDisponible,
                                      BigDecimal seuil,
                                      BigDecimal quantiteSuggeree,
                                      Integer delaiReapproJours,
                                      String urgence) {

    public static BesoinApprovisionnement calculer(String articleCode,
                                                   String libelle,
                                                   BigDecimal quantiteDisponible,
                                                   BigDecimal seuil,
                                                   BigDecimal consommationPrevue,
                                                   Integer delaiReapproJours) {
        BigDecimal dispo = quantiteDisponible == null ? BigDecimal.ZERO : quantiteDisponible;
        BigDecimal plafond = seuil == null ? BigDecimal.ZERO : seuil;
        BigDecimal conso = consommationPrevue == null ? BigDecimal.ZERO : consommationPrevue;
        BigDecimal suggeree = plafond.add(conso).subtract(dispo);
        if (suggeree.signum() < 0) {
            suggeree = BigDecimal.ZERO;
        }
        return new BesoinApprovisionnement(articleCode, libelle,
                dispo.setScale(4, RoundingMode.HALF_UP),
                plafond.setScale(4, RoundingMode.HALF_UP),
                suggeree.setScale(4, RoundingMode.HALF_UP),
                delaiReapproJours,
                urgence(dispo, plafond));
    }

    private static String urgence(BigDecimal dispo, BigDecimal seuil) {
        if (dispo.signum() <= 0) {
            return "RUPTURE";
        }
        if (dispo.compareTo(seuil) < 0) {
            return "HAUTE";
        }
        return "NORMALE";
    }

    public boolean estSousSeuil() {
        return "RUPTURE".equals(urgence) || "HAUTE".equals(urgence);
    }
}
