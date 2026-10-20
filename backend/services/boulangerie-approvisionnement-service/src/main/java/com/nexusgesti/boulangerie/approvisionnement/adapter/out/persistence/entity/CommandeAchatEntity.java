package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "commande_achat", schema = "boulangerie_approvisionnement")
public class CommandeAchatEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "fournisseur_tiers_code", nullable = false)
    private String fournisseurTiersCode;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;

    @Column(nullable = false)
    private String statut;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", nullable = false)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", nullable = false)
    private BigDecimal montantTTC;

    @Column(nullable = false)
    private String devise;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getFournisseurTiersCode() { return fournisseurTiersCode; }
    public void setFournisseurTiersCode(String fournisseurTiersCode) { this.fournisseurTiersCode = fournisseurTiersCode; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getDateLivraisonPrevue() { return dateLivraisonPrevue; }
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) { this.dateLivraisonPrevue = dateLivraisonPrevue; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
