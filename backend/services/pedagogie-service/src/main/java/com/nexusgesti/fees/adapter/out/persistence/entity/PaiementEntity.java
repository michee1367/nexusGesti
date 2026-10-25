package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "paiement", schema = "pedagogie")
public class PaiementEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String reference;

    @Column(name = "avis_id")
    private UUID avisId;

    @Column(nullable = false)
    private UUID eleve;

    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(nullable = false)
    private String moyen;

    @Column(name = "compte_debit")
    private String compteDebit;

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @Column(name = "id_evenement")
    private String idEvenement;

    @Column(nullable = false)
    private String beneficiaire;

    @Column(name = "reference_beneficiaire")
    private String referenceBeneficiaire;

    @Column(nullable = false)
    private String statut;

    @Column(name = "encaisse_par")
    private UUID encaissePar;

    @Column(name = "reste_a_rembourser", nullable = false)
    private BigDecimal resteARembourser;

    @Column(name = "coupon_genere")
    private UUID couponGenere;

    @Column(name = "ecriture_generee")
    private UUID ecritureGeneree;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "date_validation")
    private Instant dateValidation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public UUID getAvisId() { return avisId; }
    public void setAvisId(UUID avisId) { this.avisId = avisId; }
    public UUID getEleve() { return eleve; }
    public void setEleve(UUID eleve) { this.eleve = eleve; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getMoyen() { return moyen; }
    public void setMoyen(String moyen) { this.moyen = moyen; }
    public String getCompteDebit() { return compteDebit; }
    public void setCompteDebit(String compteDebit) { this.compteDebit = compteDebit; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
    public String getIdEvenement() { return idEvenement; }
    public void setIdEvenement(String idEvenement) { this.idEvenement = idEvenement; }
    public String getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(String beneficiaire) { this.beneficiaire = beneficiaire; }
    public String getReferenceBeneficiaire() { return referenceBeneficiaire; }
    public void setReferenceBeneficiaire(String referenceBeneficiaire) { this.referenceBeneficiaire = referenceBeneficiaire; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getEncaissePar() { return encaissePar; }
    public void setEncaissePar(UUID encaissePar) { this.encaissePar = encaissePar; }
    public BigDecimal getResteARembourser() { return resteARembourser; }
    public void setResteARembourser(BigDecimal resteARembourser) { this.resteARembourser = resteARembourser; }
    public UUID getCouponGenere() { return couponGenere; }
    public void setCouponGenere(UUID couponGenere) { this.couponGenere = couponGenere; }
    public UUID getEcritureGeneree() { return ecritureGeneree; }
    public void setEcritureGeneree(UUID ecritureGeneree) { this.ecritureGeneree = ecritureGeneree; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
    public Instant getDateValidation() { return dateValidation; }
    public void setDateValidation(Instant dateValidation) { this.dateValidation = dateValidation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}