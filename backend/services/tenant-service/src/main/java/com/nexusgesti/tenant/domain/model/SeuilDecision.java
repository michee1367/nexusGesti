package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class SeuilDecision {

    private UUID idSeuil;
    private UUID grilleId;
    private String libelle;
    private TypePortee portee;
    private UUID cycleId;
    private String niveau;
    private String parcours;
    private String programme;
    private BigDecimal moyenneMinimale;
    private BigDecimal moyenneMaximale;
    private BigDecimal tauxReussiteMinimale;
    private BigDecimal moyenneDeReussite;
    private Integer absencesMaximales;
    private Decision decisionResultante;
    private Integer rangMin;
    private Integer rangMax;
    private int priorite;

    public boolean contient(BigDecimal moyenne, BigDecimal tauxReussite,
                            Integer absences, Integer rang) {
        if (moyenne != null && moyenneMinimale != null && moyenne.compareTo(moyenneMinimale) < 0) {
            return false;
        }
        if (moyenne != null && moyenneMaximale != null && moyenne.compareTo(moyenneMaximale) > 0) {
            return false;
        }
        if (tauxReussite != null && tauxReussiteMinimale != null
                && tauxReussite.compareTo(tauxReussiteMinimale) < 0) {
            return false;
        }
        if (absences != null && absencesMaximales != null && absences > absencesMaximales) {
            return false;
        }
        if (rang != null && rangMin != null && rang < rangMin) {
            return false;
        }
        return rang == null || rangMax == null || rang <= rangMax;
    }

    public String getBorneBasse() {
        if (moyenneMinimale != null) {
            return moyenneMinimale.toPlainString();
        }
        if (tauxReussiteMinimale != null) {
            return tauxReussiteMinimale.toPlainString();
        }
        if (rangMin != null) {
            return String.valueOf(rangMin);
        }
        return "";
    }

    public UUID getIdSeuil() { return idSeuil; }
    public void setIdSeuil(UUID idSeuil) { this.idSeuil = idSeuil; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypePortee getPortee() { return portee; }
    public void setPortee(TypePortee portee) { this.portee = portee; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public BigDecimal getMoyenneMinimale() { return moyenneMinimale; }
    public void setMoyenneMinimale(BigDecimal moyenneMinimale) { this.moyenneMinimale = moyenneMinimale; }
    public BigDecimal getMoyenneMaximale() { return moyenneMaximale; }
    public void setMoyenneMaximale(BigDecimal moyenneMaximale) { this.moyenneMaximale = moyenneMaximale; }
    public BigDecimal getTauxReussiteMinimale() { return tauxReussiteMinimale; }
    public void setTauxReussiteMinimale(BigDecimal tauxReussiteMinimale) { this.tauxReussiteMinimale = tauxReussiteMinimale; }
    public BigDecimal getMoyenneDeReussite() { return moyenneDeReussite; }
    public void setMoyenneDeReussite(BigDecimal moyenneDeReussite) { this.moyenneDeReussite = moyenneDeReussite; }
    public Integer getAbsencesMaximales() { return absencesMaximales; }
    public void setAbsencesMaximales(Integer absencesMaximales) { this.absencesMaximales = absencesMaximales; }
    public Decision getDecisionResultante() { return decisionResultante; }
    public void setDecisionResultante(Decision decisionResultante) { this.decisionResultante = decisionResultante; }
    public Integer getRangMin() { return rangMin; }
    public void setRangMin(Integer rangMin) { this.rangMin = rangMin; }
    public Integer getRangMax() { return rangMax; }
    public void setRangMax(Integer rangMax) { this.rangMax = rangMax; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
}