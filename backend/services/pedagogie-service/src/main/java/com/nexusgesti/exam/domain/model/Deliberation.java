package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Deliberation {

    private UUID id;
    private UUID sessionId;
    private String reference;
    private LocalDate date;
    private StatutDeliberation statut = StatutDeliberation.PLANIFIEE;
    private UUID president;
    private List<UUID> membres = new ArrayList<>();
    private boolean quorumAtteint;
    private Integer grilleVersion;
    private Instant dateCloture;

    private List<Decision> decisions = new ArrayList<>();
    private List<DecisionUE> decisionsUE = new ArrayList<>();

    public Deliberation planifier() {
        this.statut = StatutDeliberation.PLANIFIEE;
        if (this.date == null) {
            this.date = LocalDate.now();
        }
        return this;
    }

    public Deliberation ouvrir() {
        if (statut != StatutDeliberation.PLANIFIEE && statut != StatutDeliberation.REPRISE) {
            throw ExamException.transitionInvalide(statut.name(), StatutDeliberation.EN_COURS.name());
        }
        this.statut = StatutDeliberation.EN_COURS;
        this.quorumAtteint = true;
        return this;
    }

    public Decision decider(UUID classeId, UUID eleveId) {
        if (statut != StatutDeliberation.EN_COURS) {
            throw ExamException.etatInvalide("Decision impossible : deliberation " + statut);
        }
        Decision decision = new Decision();
        decision.setId(UUID.randomUUID());
        decision.setDeliberationId(id);
        decision.setEleveId(eleveId);
        decision.setClasseId(classeId);
        decision.setDateDecision(LocalDate.now());
        this.decisions.add(decision);
        return decision;
    }

    public List<DecisionUE> delibererParUE(UUID ueId) {
        if (statut != StatutDeliberation.EN_COURS) {
            throw ExamException.etatInvalide("Decision impossible : deliberation " + statut);
        }
        return decisionsUE.stream().filter(d -> ueId.equals(d.getUeId())).toList();
    }

    public List<Decision> delibererParClasse() {
        return decisions;
    }

    public Deliberation cloturer() {
        if (statut != StatutDeliberation.EN_COURS) {
            throw ExamException.transitionInvalide(statut.name(), StatutDeliberation.CLOTUREE.name());
        }
        this.statut = StatutDeliberation.CLOTUREE;
        this.dateCloture = Instant.now();
        return this;
    }

    public BigDecimal getTauxReussite() {
        if (decisions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        long reussis = decisions.stream().filter(Decision::estFavorable).count();
        return BigDecimal.valueOf(reussis)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(decisions.size()), 2, RoundingMode.HALF_UP);
    }

    public void ajouterDecision(Decision decision) {
        if (decision.getId() == null) {
            decision.setId(UUID.randomUUID());
        }
        decision.setDeliberationId(id);
        this.decisions.add(decision);
    }

    public void ajouterDecisionUE(DecisionUE decisionUe) {
        if (decisionUe.getId() == null) {
            decisionUe.setId(UUID.randomUUID());
        }
        decisionUe.setDeliberationId(id);
        this.decisionsUE.add(decisionUe);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public StatutDeliberation getStatut() { return statut; }
    public void setStatut(StatutDeliberation statut) { this.statut = statut; }
    public UUID getPresident() { return president; }
    public void setPresident(UUID president) { this.president = president; }
    public List<UUID> getMembres() { return membres; }
    public void setMembres(List<UUID> membres) { this.membres = membres == null ? new ArrayList<>() : new ArrayList<>(membres); }
    public boolean isQuorumAtteint() { return quorumAtteint; }
    public void setQuorumAtteint(boolean quorumAtteint) { this.quorumAtteint = quorumAtteint; }
    public Integer getGrilleVersion() { return grilleVersion; }
    public void setGrilleVersion(Integer grilleVersion) { this.grilleVersion = grilleVersion; }
    public Instant getDateCloture() { return dateCloture; }
    public void setDateCloture(Instant dateCloture) { this.dateCloture = dateCloture; }
    public List<Decision> getDecisions() { return decisions; }
    public void setDecisions(List<Decision> decisions) { this.decisions = decisions == null ? new ArrayList<>() : new ArrayList<>(decisions); }
    public List<DecisionUE> getDecisionsUE() { return decisionsUE; }
    public void setDecisionsUE(List<DecisionUE> decisionsUE) { this.decisionsUE = decisionsUE == null ? new ArrayList<>() : new ArrayList<>(decisionsUE); }
}
