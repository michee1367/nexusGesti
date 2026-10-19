package com.nexusgesti.boulangerie.production.domain.model;

import java.math.BigDecimal;

public record MouvementStockIntention(TypeMouvementStock type,
                                      String articleCode,
                                      String lotNumero,
                                      BigDecimal quantite,
                                      BigDecimal prixMoyen,
                                      String sourceId) {
}
