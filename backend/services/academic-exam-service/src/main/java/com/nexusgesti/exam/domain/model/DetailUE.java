package com.nexusgesti.exam.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class DetailUE {

    private UUID id;
    private UUID decisionUeId;
    private UUID coursId;
    private BigDecimal moyenneCours;
    private BigDecimal coefficient;
    private boolean admis;

    public boolean estCompense() {
        return admis || (moyenneCours != null && coefficient != null
                && moyenneCours.multiply(coefficient).compareTo(BigDecimal.ZERO) >= 0);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDecisionUeId() { return decisionUeId; }
    public void setDecisionUeId(UUID decisionUeId) { this.decisionUeId = decisionUeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public BigDecimal getMoyenneCours() { return moyenneCours; }
    public void setMoyenneCours(BigDecimal moyenneCours) { this.moyenneCours = moyenneCours; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public boolean isAdmis() { return admis; }
    public void setAdmis(boolean admis) { this.admis = admis; }
}
