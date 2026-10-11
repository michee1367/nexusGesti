package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Paiement {

    private UUID idPaiement;
    private String reference;
    private UUID avis;
    private UUID eleve;
    private LocalDate date;
    private BigDecimal montant = BigDecimal.ZERO;
    private MoyenPaiement moyen;
    private String compteDebit;
    private String referenceTransaction;
    private String idEvenement;
    private TypeBeneficiaire beneficiaire = TypeBeneficiaire.ELEVE;
    private String referenceBeneficiaire;
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;
    private UUID encaissePar;
    private BigDecimal resteARembourser = BigDecimal.ZERO;
    private UUID couponGenere;
    private UUID ecritureGeneree;
    private String motifAnnulation;
    private String motifRejet;
    private Instant dateValidation;

    private List<Imputation> imputations = new ArrayList<>();
    private PayementMobileMoney mobileMoney;
    private PayementVirement virement;
    private PayementEspeces especes;
    private Remboursement remboursement;

    public Paiement valider() {
        if (statut != StatutPaiement.EN_ATTENTE && statut != StatutPaiement.A_VERIFIER) {
            throw FeesException.transitionInvalide(statut.name(), StatutPaiement.VALIDE.name());
        }
        this.statut = StatutPaiement.VALIDE;
        this.dateValidation = Instant.now();
        return this;
    }

    public Paiement rejeter(String motif) {
        if (statut == StatutPaiement.VALIDE || statut == StatutPaiement.ANNULE) {
            throw FeesException.transitionInvalide(statut.name(), StatutPaiement.REJETE.name());
        }
        this.statut = StatutPaiement.REJETE;
        this.motifRejet = motif;
        return this;
    }

    public Paiement annuler(String motif) {
        if (statut == StatutPaiement.ANNULE) {
            throw FeesException.transitionInvalide(statut.name(), StatutPaiement.ANNULE.name());
        }
        this.statut = StatutPaiement.ANNULE;
        this.motifAnnulation = motif;
        return this;
    }

    public boolean estIdempotent() {
        return idEvenement != null && !idEvenement.isBlank();
    }

    public void ajouterImputation(Imputation imputation) {
        imputation.setPaiementId(idPaiement);
        this.imputations.add(imputation);
    }

    public UUID getIdPaiement() { return idPaiement; }
    public void setIdPaiement(UUID idPaiement) { this.idPaiement = idPaiement; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public UUID getAvis() { return avis; }
    public void setAvis(UUID avis) { this.avis = avis; }
    public UUID getEleve() { return eleve; }
    public void setEleve(UUID eleve) { this.eleve = eleve; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public MoyenPaiement getMoyen() { return moyen; }
    public void setMoyen(MoyenPaiement moyen) { this.moyen = moyen; }
    public String getCompteDebit() { return compteDebit; }
    public void setCompteDebit(String compteDebit) { this.compteDebit = compteDebit; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
    public String getIdEvenement() { return idEvenement; }
    public void setIdEvenement(String idEvenement) { this.idEvenement = idEvenement; }
    public TypeBeneficiaire getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(TypeBeneficiaire beneficiaire) { this.beneficiaire = beneficiaire; }
    public String getReferenceBeneficiaire() { return referenceBeneficiaire; }
    public void setReferenceBeneficiaire(String referenceBeneficiaire) { this.referenceBeneficiaire = referenceBeneficiaire; }
    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }
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
    public List<Imputation> getImputations() { return imputations; }
    public void setImputations(List<Imputation> imputations) { this.imputations = imputations == null ? new ArrayList<>() : new ArrayList<>(imputations); }
    public PayementMobileMoney getMobileMoney() { return mobileMoney; }
    public void setMobileMoney(PayementMobileMoney mobileMoney) { this.mobileMoney = mobileMoney; }
    public PayementVirement getVirement() { return virement; }
    public void setVirement(PayementVirement virement) { this.virement = virement; }
    public PayementEspeces getEspeces() { return especes; }
    public void setEspeces(PayementEspeces especes) { this.especes = especes; }
    public Remboursement getRemboursement() { return remboursement; }
    public void setRemboursement(Remboursement remboursement) { this.remboursement = remboursement; }
}
