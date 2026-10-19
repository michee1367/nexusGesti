package com.nexusgesti.boulangerie.produit.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class IngredientRecette {

    private UUID id;
    private UUID recetteId;
    private String articleCode;
    private String libelle;
    private BigDecimal quantite = BigDecimal.ZERO;
    private String unite;
    private BigDecimal tauxPerte = BigDecimal.ZERO;
    private Integer ordre = 0;
    private boolean comptabiliseCout = true;

    public BigDecimal getQuantiteNette() {
        return quantite.setScale(4, RoundingMode.HALF_UP);
    }

    public BigDecimal getQuantiteBrute() {
        return quantite.multiply(BigDecimal.ONE.add(tauxPerte)).setScale(4, RoundingMode.HALF_UP);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRecetteId() { return recetteId; }
    public void setRecetteId(UUID recetteId) { this.recetteId = recetteId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
    public BigDecimal getTauxPerte() { return tauxPerte; }
    public void setTauxPerte(BigDecimal tauxPerte) { this.tauxPerte = tauxPerte; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public boolean isComptabiliseCout() { return comptabiliseCout; }
    public void setComptabiliseCout(boolean comptabiliseCout) { this.comptabiliseCout = comptabiliseCout; }
}
