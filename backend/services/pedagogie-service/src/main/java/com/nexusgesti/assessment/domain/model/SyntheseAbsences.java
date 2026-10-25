package com.nexusgesti.assessment.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SyntheseAbsences {

    private int seancesJustifiees;
    private int seancesInjustifiees;
    private int retardJours;
    private int retardHeures;
    private String regleApplicable;
    private boolean respecteRegle;
    private String avertissement;

    public int getTotalAbsences() {
        return seancesJustifiees + seancesInjustifiees;
    }

    public boolean estEnAlerte() {
        return !respecteRegle;
    }

    public BigDecimal getTauxAbsence(int seancesPrevues) {
        if (seancesPrevues <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(getTotalAbsences())
                .divide(BigDecimal.valueOf(seancesPrevues), 4, RoundingMode.HALF_UP);
    }

    public String getCompteRendu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Absences justifiees : ").append(seancesJustifiees)
                .append(" | injustifiees : ").append(seancesInjustifiees);
        if (retardJours > 0 || retardHeures > 0) {
            sb.append(" | retards : ").append(retardJours).append("j ")
                    .append(retardHeures).append("h");
        }
        if (regleApplicable != null) {
            sb.append(" | regle : ").append(regleApplicable)
                    .append(respecteRegle ? " (respectee)" : " (non respectee)");
        }
        if (avertissement != null && !avertissement.isBlank()) {
            sb.append(" | ").append(avertissement);
        }
        return sb.toString();
    }

    public int getSeancesJustifiees() { return seancesJustifiees; }
    public void setSeancesJustifiees(int seancesJustifiees) { this.seancesJustifiees = seancesJustifiees; }
    public int getSeancesInjustifiees() { return seancesInjustifiees; }
    public void setSeancesInjustifiees(int seancesInjustifiees) { this.seancesInjustifiees = seancesInjustifiees; }
    public int getRetardJours() { return retardJours; }
    public void setRetardJours(int retardJours) { this.retardJours = retardJours; }
    public int getRetardHeures() { return retardHeures; }
    public void setRetardHeures(int retardHeures) { this.retardHeures = retardHeures; }
    public String getRegleApplicable() { return regleApplicable; }
    public void setRegleApplicable(String regleApplicable) { this.regleApplicable = regleApplicable; }
    public boolean isRespecteRegle() { return respecteRegle; }
    public void setRespecteRegle(boolean respecteRegle) { this.respecteRegle = respecteRegle; }
    public String getAvertissement() { return avertissement; }
    public void setAvertissement(String avertissement) { this.avertissement = avertissement; }
}
