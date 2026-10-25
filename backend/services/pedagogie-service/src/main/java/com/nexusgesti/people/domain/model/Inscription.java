package com.nexusgesti.people.domain.model;

import com.nexusgesti.people.domain.PeopleException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Inscription {

    private UUID idInscription;
    private UUID eleveId;
    private UUID anneeId;
    private UUID classeId;
    private LocalDate dateInscription;
    private StatutInscription statut;
    private boolean redoublant;
    private boolean nouveau;
    private String ecoleOrigine;
    private BigDecimal moyenneAnneePrecedente;
    private Integer classementAnneePrecedente;
    private boolean fraisRegles;
    private String motifAnnulation;

    public Inscription valider() {
        if (statut != StatutInscription.EN_ATTENTE && statut != StatutInscription.SUSPENDUE) {
            throw PeopleException.transitionInvalide(String.valueOf(statut), StatutInscription.VALIDE.name());
        }
        this.statut = StatutInscription.VALIDE;
        return this;
    }

    public Inscription annuler(String motif) {
        if (statut == StatutInscription.ANNULEE) {
            throw PeopleException.transitionInvalide(String.valueOf(statut), StatutInscription.ANNULEE.name());
        }
        this.statut = StatutInscription.ANNULEE;
        this.motifAnnulation = motif;
        return this;
    }

    public boolean estEffectivementInscrit() {
        return statut == StatutInscription.VALIDE;
    }

    public UUID getIdInscription() { return idInscription; }
    public void setIdInscription(UUID idInscription) { this.idInscription = idInscription; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public StatutInscription getStatut() { return statut; }
    public void setStatut(StatutInscription statut) { this.statut = statut; }
    public boolean isRedoublant() { return redoublant; }
    public void setRedoublant(boolean redoublant) { this.redoublant = redoublant; }
    public boolean isNouveau() { return nouveau; }
    public void setNouveau(boolean nouveau) { this.nouveau = nouveau; }
    public String getEcoleOrigine() { return ecoleOrigine; }
    public void setEcoleOrigine(String ecoleOrigine) { this.ecoleOrigine = ecoleOrigine; }
    public BigDecimal getMoyenneAnneePrecedente() { return moyenneAnneePrecedente; }
    public void setMoyenneAnneePrecedente(BigDecimal moyenneAnneePrecedente) { this.moyenneAnneePrecedente = moyenneAnneePrecedente; }
    public Integer getClassementAnneePrecedente() { return classementAnneePrecedente; }
    public void setClassementAnneePrecedente(Integer classementAnneePrecedente) { this.classementAnneePrecedente = classementAnneePrecedente; }
    public boolean isFraisRegles() { return fraisRegles; }
    public void setFraisRegles(boolean fraisRegles) { this.fraisRegles = fraisRegles; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
}
