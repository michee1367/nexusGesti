package com.nexusgesti.enrollment.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class RegleInscription {

    private UUID idRegle;
    private String libelle;
    private UUID programmeId;
    private String condition;
    private BigDecimal creditsMin;
    private BigDecimal moyenneMin;
    private Integer creneauAge;
    private int priorite;

    public DecisionInscription evaluer(Candidat candidat) {
        if (!candidat.aRempli(condition)) {
            return DecisionInscription.INELIGIBLE;
        }
        if (creditsMin != null && (candidat.getCreditsValides() == null
                || candidat.getCreditsValides().compareTo(creditsMin) < 0)) {
            return DecisionInscription.INELIGIBLE;
        }
        if (moyenneMin != null && (candidat.getMoyenneGenerale() == null
                || candidat.getMoyenneGenerale().compareTo(moyenneMin) < 0)) {
            return DecisionInscription.INELIGIBLE;
        }
        if (creneauAge != null && candidat.getAge() != null && candidat.getAge() > creneauAge) {
            return DecisionInscription.INELIGIBLE;
        }
        return DecisionInscription.ELIGIBLE;
    }

    public boolean sAppliqueA(UUID programme) {
        return programmeId == null || programmeId.equals(programme);
    }

    public UUID getIdRegle() { return idRegle; }
    public void setIdRegle(UUID idRegle) { this.idRegle = idRegle; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public BigDecimal getCreditsMin() { return creditsMin; }
    public void setCreditsMin(BigDecimal creditsMin) { this.creditsMin = creditsMin; }
    public BigDecimal getMoyenneMin() { return moyenneMin; }
    public void setMoyenneMin(BigDecimal moyenneMin) { this.moyenneMin = moyenneMin; }
    public Integer getCreneauAge() { return creneauAge; }
    public void setCreneauAge(Integer creneauAge) { this.creneauAge = creneauAge; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
}
