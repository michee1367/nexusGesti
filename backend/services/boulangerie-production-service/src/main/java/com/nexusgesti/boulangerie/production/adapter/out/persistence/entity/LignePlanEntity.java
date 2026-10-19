package com.nexusgesti.boulangerie.production.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_plan", schema = "boulangerie_production")
public class LignePlanEntity {

    @Id
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "recette_code", nullable = false)
    private String recetteCode;

    @Column(name = "version_recette")
    private Integer versionRecette;

    @Column(name = "quantite_prevue", nullable = false)
    private BigDecimal quantitePrevue;

    @Column(name = "produit_code")
    private String produitCode;

    @Column(name = "four_code")
    private String fourCode;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
    public String getRecetteCode() { return recetteCode; }
    public void setRecetteCode(String recetteCode) { this.recetteCode = recetteCode; }
    public Integer getVersionRecette() { return versionRecette; }
    public void setVersionRecette(Integer versionRecette) { this.versionRecette = versionRecette; }
    public BigDecimal getQuantitePrevue() { return quantitePrevue; }
    public void setQuantitePrevue(BigDecimal quantitePrevue) { this.quantitePrevue = quantitePrevue; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getFourCode() { return fourCode; }
    public void setFourCode(String fourCode) { this.fourCode = fourCode; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
