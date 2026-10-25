package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Rapprochement(String commandeNumero,
                            String statutCommande,
                            BigDecimal montantCommandeTTC,
                            BigDecimal montantReceptionsTTC,
                            boolean concordant,
                            List<Ecart> ecarts) {

    public record Ecart(String articleCode,
                        BigDecimal quantiteCommandee,
                        BigDecimal quantiteRecue,
                        BigDecimal prixCommande,
                        BigDecimal prixAchat,
                        BigDecimal ecartPrixAchat) {
    }
}
