package com.nexusgesti.boulangerie.production.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class LigneConsommation {

    private UUID id;
    private UUID fourneeId;
    private String articleCode;
    private String lotNumero;
    private BigDecimal quantiteTheorique = BigDecimal.ZERO;
    private BigDecimal quantiteReelle = BigDecimal.ZERO;
    private BigDecimal prixMoyen = BigDecimal.ZERO;

    public BigDecimal getEcart() {
        return quantiteTheorique.subtract(quantiteReelle);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFourneeId() { return fourneeId; }
    public void setFourneeId(UUID fourneeId) { this.fourneeId = fourneeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLotNumero() { return lotNumero; }
    public void setLotNumero(String lotNumero) { this.lotNumero = lotNumero; }
    public BigDecimal getQuantiteTheorique() { return quantiteTheorique; }
    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique == null ? BigDecimal.ZERO : quantiteTheorique;
    }
    public BigDecimal getQuantiteReelle() { return quantiteReelle; }
    public void setQuantiteReelle(BigDecimal quantiteReelle) {
        this.quantiteReelle = quantiteReelle == null ? BigDecimal.ZERO : quantiteReelle;
    }
    public BigDecimal getPrixMoyen() { return prixMoyen; }
    public void setPrixMoyen(BigDecimal prixMoyen) {
        this.prixMoyen = prixMoyen == null ? BigDecimal.ZERO : prixMoyen;
    }
}
