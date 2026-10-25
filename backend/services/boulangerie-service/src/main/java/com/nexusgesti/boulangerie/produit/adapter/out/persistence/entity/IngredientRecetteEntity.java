package com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ingredient_recette", schema = "boulangerie")
public class IngredientRecetteEntity {

    @Id
    private UUID id;

    @Column(name = "recette_id", nullable = false)
    private UUID recetteId;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private BigDecimal quantite;

    @Column(nullable = false)
    private String unite;

    @Column(name = "taux_perte", nullable = false)
    private BigDecimal tauxPerte;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "comptabilise_cout", nullable = false)
    private boolean comptabiliseCout;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
