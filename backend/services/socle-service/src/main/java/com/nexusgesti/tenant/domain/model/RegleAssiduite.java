package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class RegleAssiduite {

    private UUID idRegle;
    private UUID grilleId;
    private TypePortee portee;
    private UUID cycleId;
    private String niveau;
    private Integer absencesMaxParPeriode;
    private Integer absencesParJourMax;
    private BigDecimal tauxPresenceMinimale;
    private boolean absencesJustifieesExonerees;
    private boolean retardCompteCommeAbsence;
    private Integer seuilRetardMinutes;
    private Decision consequence;
    private int priorite;

    public boolean estRespectee(Integer absences, Integer seances) {
        int a = absences == null ? 0 : absences;
        if (absencesMaxParPeriode != null && a > absencesMaxParPeriode) {
            return false;
        }
        if (tauxPresenceMinimale != null
                && getTauxPresence(seances, a).compareTo(tauxPresenceMinimale) < 0) {
            return false;
        }
        return true;
    }

    public BigDecimal getTauxPresence(Integer seances, Integer absences) {
        int s = seances == null ? 0 : seances;
        int a = absences == null ? 0 : absences;
        if (s <= 0) {
            return BigDecimal.ZERO;
        }
        if (a >= s) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(s - a)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(s), 2, RoundingMode.HALF_UP);
    }

    public UUID getIdRegle() { return idRegle; }
    public void setIdRegle(UUID idRegle) { this.idRegle = idRegle; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public TypePortee getPortee() { return portee; }
    public void setPortee(TypePortee portee) { this.portee = portee; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public Integer getAbsencesMaxParPeriode() { return absencesMaxParPeriode; }
    public void setAbsencesMaxParPeriode(Integer absencesMaxParPeriode) { this.absencesMaxParPeriode = absencesMaxParPeriode; }
    public Integer getAbsencesParJourMax() { return absencesParJourMax; }
    public void setAbsencesParJourMax(Integer absencesParJourMax) { this.absencesParJourMax = absencesParJourMax; }
    public BigDecimal getTauxPresenceMinimale() { return tauxPresenceMinimale; }
    public void setTauxPresenceMinimale(BigDecimal tauxPresenceMinimale) { this.tauxPresenceMinimale = tauxPresenceMinimale; }
    public boolean isAbsencesJustifieesExonerees() { return absencesJustifieesExonerees; }
    public void setAbsencesJustifieesExonerees(boolean absencesJustifieesExonerees) { this.absencesJustifieesExonerees = absencesJustifieesExonerees; }
    public boolean isRetardCompteCommeAbsence() { return retardCompteCommeAbsence; }
    public void setRetardCompteCommeAbsence(boolean retardCompteCommeAbsence) { this.retardCompteCommeAbsence = retardCompteCommeAbsence; }
    public Integer getSeuilRetardMinutes() { return seuilRetardMinutes; }
    public void setSeuilRetardMinutes(Integer seuilRetardMinutes) { this.seuilRetardMinutes = seuilRetardMinutes; }
    public Decision getConsequence() { return consequence; }
    public void setConsequence(Decision consequence) { this.consequence = consequence; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
}