package com.nexusgesti.structure.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class CoursECUE {

    private UUID idCours;
    private String code;
    private String libelle;
    private UUID ueId;
    private UUID classeId;
    private UUID matiereId;
    private TypeEvaluation type;
    private BigDecimal volumeHoraire;
    private BigDecimal credits;
    private BigDecimal coefficient;
    private boolean hebdomadaire;
    private String regroupement;

    public boolean estECUE() {
        return ueId != null;
    }

    public boolean estMatiere() {
        return matiereId != null;
    }

    public UUID getIdCours() { return idCours; }
    public void setIdCours(UUID idCours) { this.idCours = idCours; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public TypeEvaluation getType() { return type; }
    public void setType(TypeEvaluation type) { this.type = type; }
    public BigDecimal getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(BigDecimal volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public boolean isHebdomadaire() { return hebdomadaire; }
    public void setHebdomadaire(boolean hebdomadaire) { this.hebdomadaire = hebdomadaire; }
    public String getRegroupement() { return regroupement; }
    public void setRegroupement(String regroupement) { this.regroupement = regroupement; }
}
