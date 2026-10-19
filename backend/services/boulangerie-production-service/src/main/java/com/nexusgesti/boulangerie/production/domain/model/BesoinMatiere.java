package com.nexusgesti.boulangerie.production.domain.model;

import java.math.BigDecimal;

public record BesoinMatiere(String articleCode,
                            String libelle,
                            BigDecimal quantiteNette,
                            BigDecimal quantiteBrute,
                            String unite) {
}
