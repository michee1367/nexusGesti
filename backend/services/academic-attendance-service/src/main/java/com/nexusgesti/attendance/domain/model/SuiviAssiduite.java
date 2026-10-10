package com.nexusgesti.attendance.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public class SuiviAssiduite {

    private UUID eleveId;
    private UUID anneeId;
    private UUID periodeId;
    private Integer nbSeancesPrevues;
    private Integer nbSeancesTenues;
    private BigDecimal nbHeuresPrevues;
    private BigDecimal nbHeuresAbsentes;
    private Integer nbAbsencesJustifiees;
    private Integer nbAbsencesInjustifiees;
    private Integer nbRetards;
    private Integer minutesRetardTotal;
    private BigDecimal tauxPresence;
    private Boolean respecteRegle;
    private String regleApplicable;
    private Instant dernierCalcul;

    public SuiviAssiduite recalculer() {
        this.tauxPresence = getTauxPresence();
        this.dernierCalcul = Instant.now();
        return this;
    }

    public BigDecimal getTauxPresence() {
        int tenues = nz(nbSeancesTenues);
        if (tenues <= 0) {
            return BigDecimal.ZERO;
        }
        int absences = nz(nbAbsencesJustifiees) + nz(nbAbsencesInjustifiees);
        int presentes = Math.max(0, tenues - absences);
        return BigDecimal.valueOf(presentes * 100.0)
                .divide(BigDecimal.valueOf(tenues), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTauxAbsenceJustifiee() {
        int justifiees = nz(nbAbsencesJustifiees);
        int total = justifiees + nz(nbAbsencesInjustifiees);
        if (total <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(justifiees * 100.0)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    public boolean estEnAlerte() {
        return respecteRegle != null && !respecteRegle;
    }

    private int nz(Integer value) {
        return value == null ? 0 : value;
    }

    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public Integer getNbSeancesPrevues() { return nbSeancesPrevues; }
    public void setNbSeancesPrevues(Integer nbSeancesPrevues) { this.nbSeancesPrevues = nbSeancesPrevues; }
    public Integer getNbSeancesTenues() { return nbSeancesTenues; }
    public void setNbSeancesTenues(Integer nbSeancesTenues) { this.nbSeancesTenues = nbSeancesTenues; }
    public BigDecimal getNbHeuresPrevues() { return nbHeuresPrevues; }
    public void setNbHeuresPrevues(BigDecimal nbHeuresPrevues) { this.nbHeuresPrevues = nbHeuresPrevues; }
    public BigDecimal getNbHeuresAbsentes() { return nbHeuresAbsentes; }
    public void setNbHeuresAbsentes(BigDecimal nbHeuresAbsentes) { this.nbHeuresAbsentes = nbHeuresAbsentes; }
    public Integer getNbAbsencesJustifiees() { return nbAbsencesJustifiees; }
    public void setNbAbsencesJustifiees(Integer nbAbsencesJustifiees) { this.nbAbsencesJustifiees = nbAbsencesJustifiees; }
    public Integer getNbAbsencesInjustifiees() { return nbAbsencesInjustifiees; }
    public void setNbAbsencesInjustifiees(Integer nbAbsencesInjustifiees) { this.nbAbsencesInjustifiees = nbAbsencesInjustifiees; }
    public Integer getNbRetards() { return nbRetards; }
    public void setNbRetards(Integer nbRetards) { this.nbRetards = nbRetards; }
    public Integer getMinutesRetardTotal() { return minutesRetardTotal; }
    public void setMinutesRetardTotal(Integer minutesRetardTotal) { this.minutesRetardTotal = minutesRetardTotal; }
    public void setTauxPresence(BigDecimal tauxPresence) { this.tauxPresence = tauxPresence; }
    public Boolean getRespecteRegle() { return respecteRegle; }
    public void setRespecteRegle(Boolean respecteRegle) { this.respecteRegle = respecteRegle; }
    public String getRegleApplicable() { return regleApplicable; }
    public void setRegleApplicable(String regleApplicable) { this.regleApplicable = regleApplicable; }
    public Instant getDernierCalcul() { return dernierCalcul; }
    public void setDernierCalcul(Instant dernierCalcul) { this.dernierCalcul = dernierCalcul; }
}
