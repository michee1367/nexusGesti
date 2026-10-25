package com.nexusgesti.boulangerie.production.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class LignePlan {

    private UUID id;
    private UUID planId;
    private String recetteCode;
    private Integer versionRecette;
    private BigDecimal quantitePrevue = BigDecimal.ZERO;
    private String produitCode;
    private String fourCode;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public String getRecetteCode() { return recetteCode; }
    public void setRecetteCode(String recetteCode) { this.recetteCode = recetteCode; }
    public Integer getVersionRecette() { return versionRecette; }
    public void setVersionRecette(Integer versionRecette) { this.versionRecette = versionRecette; }
    public BigDecimal getQuantitePrevue() { return quantitePrevue; }
    public void setQuantitePrevue(BigDecimal quantitePrevue) {
        this.quantitePrevue = quantitePrevue == null ? BigDecimal.ZERO : quantitePrevue;
    }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getFourCode() { return fourCode; }
    public void setFourCode(String fourCode) { this.fourCode = fourCode; }
}
