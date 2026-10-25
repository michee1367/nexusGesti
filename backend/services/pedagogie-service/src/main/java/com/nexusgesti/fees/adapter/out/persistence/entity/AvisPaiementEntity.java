package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "avis_paiement", schema = "pedagogie")
public class AvisPaiementEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private UUID eleve;

    private UUID annee;

    private UUID classe;

    @Column(name = "grille_id")
    private UUID grilleId;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "date_echeance_globale")
    private LocalDate dateEcheanceGlobale;

    @Column(nullable = false)
    private String statut;

    @Column(name = "montant_brut", nullable = false)
    private BigDecimal montantBrut;

    @Column(name = "montant_reduction", nullable = false)
    private BigDecimal montantReduction;

    @Column(name = "montant_net", nullable = false)
    private BigDecimal montantNet;

    @Column(name = "montant_regle", nullable = false)
    private BigDecimal montantRegle;

    @Column(name = "solde_du", nullable = false)
    private BigDecimal soldeDu;

    private String devise;

    @Column(name = "taux_change")
    private BigDecimal tauxChange;

    private UUID journalier;

    @Column(name = "ecriture_generee")
    private UUID ecritureGeneree;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public UUID getEleve() { return eleve; }
    public void setEleve(UUID eleve) { this.eleve = eleve; }
    public UUID getAnnee() { return annee; }
    public void setAnnee(UUID annee) { this.annee = annee; }
    public UUID getClasse() { return classe; }
    public void setClasse(UUID classe) { this.classe = classe; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
    public LocalDate getDateEcheanceGlobale() { return dateEcheanceGlobale; }
    public void setDateEcheanceGlobale(LocalDate dateEcheanceGlobale) { this.dateEcheanceGlobale = dateEcheanceGlobale; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getMontantBrut() { return montantBrut; }
    public void setMontantBrut(BigDecimal montantBrut) { this.montantBrut = montantBrut; }
    public BigDecimal getMontantReduction() { return montantReduction; }
    public void setMontantReduction(BigDecimal montantReduction) { this.montantReduction = montantReduction; }
    public BigDecimal getMontantNet() { return montantNet; }
    public void setMontantNet(BigDecimal montantNet) { this.montantNet = montantNet; }
    public BigDecimal getMontantRegle() { return montantRegle; }
    public void setMontantRegle(BigDecimal montantRegle) { this.montantRegle = montantRegle; }
    public BigDecimal getSoldeDu() { return soldeDu; }
    public void setSoldeDu(BigDecimal soldeDu) { this.soldeDu = soldeDu; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public BigDecimal getTauxChange() { return tauxChange; }
    public void setTauxChange(BigDecimal tauxChange) { this.tauxChange = tauxChange; }
    public UUID getJournalier() { return journalier; }
    public void setJournalier(UUID journalier) { this.journalier = journalier; }
    public UUID getEcritureGeneree() { return ecritureGeneree; }
    public void setEcritureGeneree(UUID ecritureGeneree) { this.ecritureGeneree = ecritureGeneree; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}