package com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_vente", schema = "boulangerie")
public class LigneVenteEntity {

    @Id
    private UUID id;

    @Column(name = "vente_id", nullable = false)
    private UUID venteId;

    @Column(name = "produit_code", nullable = false)
    private String produitCode;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire_ht", nullable = false)
    private BigDecimal prixUnitaireHT;

    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTva;

    @Column(name = "remise_pct", nullable = false)
    private BigDecimal remisePct;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", nullable = false)
    private BigDecimal montantTVA;

    @Column(name = "lot_numero")
    private String lotNumero;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVenteId() { return venteId; }
    public void setVenteId(UUID venteId) { this.venteId = venteId; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) { this.prixUnitaireHT = prixUnitaireHT; }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }
    public BigDecimal getRemisePct() { return remisePct; }
    public void setRemisePct(BigDecimal remisePct) { this.remisePct = remisePct; }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }
    public String getLotNumero() { return lotNumero; }
    public void setLotNumero(String lotNumero) { this.lotNumero = lotNumero; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
