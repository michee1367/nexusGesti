package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fournisseur", schema = "boulangerie_approvisionnement")
public class FournisseurEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(name = "tiers_code")
    private String tiersCode;

    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    @Column(name = "delai_livraison_jours", nullable = false)
    private Integer delaiLivraisonJours;

    @Column(name = "conditions_reglement")
    private String conditionsReglement;

    @Column(name = "categorie_produits")
    private String categorieProduits;

    @Column(name = "montant_mini_commande")
    private BigDecimal montantMiniCommande;

    @Column(nullable = false)
    private Boolean actif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTiersCode() { return tiersCode; }
    public void setTiersCode(String tiersCode) { this.tiersCode = tiersCode; }
    public String getRaisonSociale() { return raisonSociale; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public Integer getDelaiLivraisonJours() { return delaiLivraisonJours; }
    public void setDelaiLivraisonJours(Integer delaiLivraisonJours) { this.delaiLivraisonJours = delaiLivraisonJours; }
    public String getConditionsReglement() { return conditionsReglement; }
    public void setConditionsReglement(String conditionsReglement) { this.conditionsReglement = conditionsReglement; }
    public String getCategorieProduits() { return categorieProduits; }
    public void setCategorieProduits(String categorieProduits) { this.categorieProduits = categorieProduits; }
    public BigDecimal getMontantMiniCommande() { return montantMiniCommande; }
    public void setMontantMiniCommande(BigDecimal montantMiniCommande) { this.montantMiniCommande = montantMiniCommande; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
