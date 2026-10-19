package com.nexusgesti.boulangerie.production.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class PerteProduction {

    private UUID idPerte;
    private UUID fourneeId;
    private String articleCode;
    private BigDecimal quantite = BigDecimal.ZERO;
    private TypePerte typePerte;
    private String motif;
    private BigDecimal valorisation = BigDecimal.ZERO;

    public void valoriser(BigDecimal prixMoyen) {
        BigDecimal prix = prixMoyen == null ? BigDecimal.ZERO : prixMoyen;
        this.valorisation = quantite.multiply(prix).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getIdPerte() { return idPerte; }
    public void setIdPerte(UUID idPerte) { this.idPerte = idPerte; }
    public UUID getFourneeId() { return fourneeId; }
    public void setFourneeId(UUID fourneeId) { this.fourneeId = fourneeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite == null ? BigDecimal.ZERO : quantite;
    }
    public TypePerte getTypePerte() { return typePerte; }
    public void setTypePerte(TypePerte typePerte) { this.typePerte = typePerte; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public BigDecimal getValorisation() { return valorisation; }
    public void setValorisation(BigDecimal valorisation) {
        this.valorisation = valorisation == null ? BigDecimal.ZERO : valorisation;
    }
}
