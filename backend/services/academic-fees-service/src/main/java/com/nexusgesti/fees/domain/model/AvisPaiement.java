package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AvisPaiement {

    private UUID idAvis;
    private String reference;
    private UUID eleve;
    private UUID annee;
    private UUID classe;
    private UUID grilleId;
    private LocalDate dateEmission;
    private LocalDate dateEcheanceGlobale;
    private StatutAvis statut = StatutAvis.BROUILLON;
    private BigDecimal montantBrut = BigDecimal.ZERO;
    private BigDecimal montantReduction = BigDecimal.ZERO;
    private BigDecimal montantNet = BigDecimal.ZERO;
    private BigDecimal montantRegle = BigDecimal.ZERO;
    private BigDecimal soldeDu = BigDecimal.ZERO;
    private String devise;
    private BigDecimal tauxChange;
    private UUID journalier;
    private UUID ecritureGeneree;
    private String motifAnnulation;

    private List<LigneFrais> lignes = new ArrayList<>();

    public AvisPaiement valider() {
        if (statut == StatutAvis.ANNULE) {
            throw FeesException.transitionInvalide(statut.name(), StatutAvis.EMIS.name());
        }
        if (statut == StatutAvis.BROUILLON) {
            this.statut = StatutAvis.EMIS;
        }
        recalculerSolde();
        return this;
    }

    public AvisPaiement emettre() {
        if (statut == StatutAvis.ANNULE) {
            throw FeesException.transitionInvalide(statut.name(), StatutAvis.EMIS.name());
        }
        if (statut == StatutAvis.BROUILLON) {
            this.statut = StatutAvis.EMIS;
        }
        if (this.dateEmission == null) {
            this.dateEmission = LocalDate.now();
        }
        recalculerSolde();
        return this;
    }

    public AvisPaiement recalculerSolde() {
        this.montantBrut = lignes.stream()
                .map(LigneFrais::getMontantBrut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.montantReduction = lignes.stream()
                .map(LigneFrais::getMontantReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.montantNet = lignes.stream()
                .map(LigneFrais::getMontantNet)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.soldeDu = this.montantNet.subtract(this.montantRegle);
        if (soldeDu.signum() <= 0 && statut != StatutAvis.ANNULE && statut != StatutAvis.BROUILLON) {
            this.statut = StatutAvis.REGLE;
        } else if (montantRegle.signum() > 0 && statut != StatutAvis.ANNULE) {
            this.statut = StatutAvis.PARTIELLEMENT_REGLE;
        }
        return this;
    }

    public boolean estSolde() {
        return soldeDu == null || soldeDu.signum() <= 0;
    }

    public boolean estEnRetard(LocalDate date) {
        return dateEcheanceGlobale != null
                && date != null
                && date.isAfter(dateEcheanceGlobale)
                && !estSolde();
    }

    public int getJoursRetard(LocalDate date) {
        if (dateEcheanceGlobale == null || date == null || !date.isAfter(dateEcheanceGlobale)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dateEcheanceGlobale, date);
    }

    public AvisPaiement annuler(String motif) {
        if (statut == StatutAvis.REGLE) {
            throw FeesException.etatInvalide("Avis deja solde : annulation interdite");
        }
        this.statut = StatutAvis.ANNULE;
        this.motifAnnulation = motif;
        return this;
    }

    public AvisPaiement appliquerRemise(UUID reductionId, BigDecimal montantRemise) {
        if (montantRemise == null || montantRemise.signum() <= 0) {
            return this;
        }
        if (montantBrut.signum() <= 0) {
            return this;
        }
        BigDecimal totalReduction = BigDecimal.ZERO;
        for (LigneFrais ligne : lignes) {
            BigDecimal part = ligne.getMontantBrut()
                    .multiply(montantRemise)
                    .divide(montantBrut, 2, RoundingMode.HALF_UP);
            ligne.setMontantReduction(ligne.getMontantReduction().add(part));
            ligne.calculerNet();
            if (montantRegle.signum() <= 0 && ligne.getEcheances() != null && !ligne.getEcheances().isEmpty()) {
                ligne.genererEcheances();
            }
            totalReduction = totalReduction.add(part);
        }
        this.montantReduction = totalReduction;
        recalculerSolde();
        return this;
    }

    public void enregistrerReglement(BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) {
            return;
        }
        this.montantRegle = this.montantRegle.add(montant);
        recalculerSolde();
    }

    public void ajouterLigne(LigneFrais ligne) {
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        ligne.setAvisId(idAvis);
        this.lignes.add(ligne);
        recalculerSolde();
    }

    public UUID getIdAvis() { return idAvis; }
    public void setIdAvis(UUID idAvis) { this.idAvis = idAvis; }
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
    public StatutAvis getStatut() { return statut; }
    public void setStatut(StatutAvis statut) { this.statut = statut; }
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
    public List<LigneFrais> getLignes() { return lignes; }
    public void setLignes(List<LigneFrais> lignes) { this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes); }
}
