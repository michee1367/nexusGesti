package com.nexusgesti.boulangerie.produit.domain.model;

import java.math.BigDecimal;

public record CoutRecette(BigDecimal coutMatiere,
                          BigDecimal coutRevient,
                          boolean coutMatiereDisponible) {
}
