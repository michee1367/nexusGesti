package com.nexusgesti.exam.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecisionUE {

    private UUID id;
    private UUID deliberationId;
    private UUID eleveId;
    private UUID ueId;
    private BigDecimal moyenneUE;
    private BigDecimal credits;
    private DecisionType decision;
    private List<DetailUE> details = new ArrayList<>();

    public boolean estAcquise() {
        return decision != null && decision.estFavorable();
    }

    public BigDecimal getCreditsAcquis() {
        return estAcquise() ? credits : BigDecimal.ZERO;
    }

    public boolean estCompensee() {
        return !details.isEmpty() && details.stream().anyMatch(DetailUE::estCompense);
    }

    public void ajouterDetail(DetailUE detail) {
        if (detail.getId() == null) {
            detail.setId(UUID.randomUUID());
        }
        detail.setDecisionUeId(id);
        this.details.add(detail);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeliberationId() { return deliberationId; }
    public void setDeliberationId(UUID deliberationId) { this.deliberationId = deliberationId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public BigDecimal getMoyenneUE() { return moyenneUE; }
    public void setMoyenneUE(BigDecimal moyenneUE) { this.moyenneUE = moyenneUE; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public DecisionType getDecision() { return decision; }
    public void setDecision(DecisionType decision) { this.decision = decision; }
    public List<DetailUE> getDetails() { return details; }
    public void setDetails(List<DetailUE> details) { this.details = details == null ? new ArrayList<>() : new ArrayList<>(details); }
}
