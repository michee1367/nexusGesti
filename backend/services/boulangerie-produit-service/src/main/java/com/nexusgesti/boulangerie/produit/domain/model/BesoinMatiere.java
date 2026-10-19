package com.nexusgesti.boulangerie.produit.domain.model;

import java.math.BigDecimal;

public record BesoinMatiere(String articleCode,
                           String libelle,
                           BigDecimal quantiteNette,
                           BigDecimal quantiteBrute,
                           String unite) {
}
