package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Epreuve {

    private UUID idEpreuve;
    private UUID sessionId;
    private String reference;
    private String libelle;
    private UUID matiereId;
    private UUID ueId;
    private TypeEpreuve type;
    private LocalDate date;
    private LocalTime heureDebut;
    private Integer dureeMinutes;
    private String bareme;
    private BigDecimal coefficient;
    private StatutEpreuve statut = StatutEpreuve.PROJETEE;
    private boolean comptabiliseDansMoyenne = true;
    private UUID periodeRattachement;
    private UUID evaluationOrigine;
    private UUID salleParDefaut;
    private String consignes;
    private boolean anonymise;
    private BigDecimal noteEliminatoire;
    private boolean declareeHorsMoyenne;

    public Epreuve valider() {
        if (statut == StatutEpreuve.ANNULEE) {
            throw ExamException.transitionInvalide(statut.name(), StatutEpreuve.VALIDEE.name());
        }
        this.statut = StatutEpreuve.VALIDEE;
        return this;
    }

    public boolean estHorsMoyenne() {
        return declareeHorsMoyenne || !comptabiliseDansMoyenne || periodeRattachement == null;
    }

    public boolean estSessionAnnuelle() {
        return periodeRattachement == null;
    }

    public BigDecimal getPointsMax() {
        if (bareme == null) {
            return null;
        }
        try {
            return new BigDecimal(bareme.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean estAnonymisee() {
        return anonymise;
    }

    public Epreuve rattacherPeriode(UUID periode) {
        this.periodeRattachement = periode;
        return this;
    }

    public Epreuve declarerHorsMoyenne() {
        this.declareeHorsMoyenne = true;
        this.comptabiliseDansMoyenne = false;
        return this;
    }

    public UUID getIdEpreuve() { return idEpreuve; }
    public void setIdEpreuve(UUID idEpreuve) { this.idEpreuve = idEpreuve; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public TypeEpreuve getType() { return type; }
    public void setType(TypeEpreuve type) { this.type = type; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public String getBareme() { return bareme; }
    public void setBareme(String bareme) { this.bareme = bareme; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public StatutEpreuve getStatut() { return statut; }
    public void setStatut(StatutEpreuve statut) { this.statut = statut; }
    public boolean isComptabiliseDansMoyenne() { return comptabiliseDansMoyenne; }
    public void setComptabiliseDansMoyenne(boolean comptabiliseDansMoyenne) { this.comptabiliseDansMoyenne = comptabiliseDansMoyenne; }
    public UUID getPeriodeRattachement() { return periodeRattachement; }
    public void setPeriodeRattachement(UUID periodeRattachement) { this.periodeRattachement = periodeRattachement; }
    public UUID getEvaluationOrigine() { return evaluationOrigine; }
    public void setEvaluationOrigine(UUID evaluationOrigine) { this.evaluationOrigine = evaluationOrigine; }
    public UUID getSalleParDefaut() { return salleParDefaut; }
    public void setSalleParDefaut(UUID salleParDefaut) { this.salleParDefaut = salleParDefaut; }
    public String getConsignes() { return consignes; }
    public void setConsignes(String consignes) { this.consignes = consignes; }
    public boolean isAnonymise() { return anonymise; }
    public void setAnonymise(boolean anonymise) { this.anonymise = anonymise; }
    public BigDecimal getNoteEliminatoire() { return noteEliminatoire; }
    public void setNoteEliminatoire(BigDecimal noteEliminatoire) { this.noteEliminatoire = noteEliminatoire; }
    public boolean isDeclareeHorsMoyenne() { return declareeHorsMoyenne; }
    public void setDeclareeHorsMoyenne(boolean declareeHorsMoyenne) { this.declareeHorsMoyenne = declareeHorsMoyenne; }
}
