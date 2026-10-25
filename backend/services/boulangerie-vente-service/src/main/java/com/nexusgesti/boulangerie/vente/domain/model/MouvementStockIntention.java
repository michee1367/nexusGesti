package com.nexusgesti.boulangerie.vente.domain.model;

import java.math.BigDecimal;

public record MouvementStockIntention(TypeMouvementStock type,
                                      String articleCode,
                                      String lotNumero,
                                      BigDecimal quantite,
                                      BigDecimal prixAchat,
                                      String sourceId) {
}
