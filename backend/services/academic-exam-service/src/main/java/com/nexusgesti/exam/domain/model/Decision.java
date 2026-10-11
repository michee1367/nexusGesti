package com.nexusgesti.exam.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Decision {

    private UUID id;
    private UUID deliberationId;
    private UUID eleveId;
    private UUID classeId;
    private BigDecimal moyenneGenerale;
    private BigDecimal moyennePeriode;
    private Integer rang;
    private Integer effectif;
    private BigDecimal tauxReussite;
    private Integer nbAbsences;
    private DecisionType decision;
    private String mention;
    private String seuilApplique;
    private DecisionCode cr;
    private String motif;
    private LocalDate dateDecision;
    private boolean notifie;
    private Instant dateNotification;

    public String getLibelleDecision() {
        return decision == null ? null : decision.libelle();
    }

    public boolean estFavorable() {
        return decision != null && decision.estFavorable();
    }

    public String getConsequenceInscription() {
        return decision == null ? null : decision.consequenceInscription();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeliberationId() { return deliberationId; }
    public void setDeliberationId(UUID deliberationId) { this.deliberationId = deliberationId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public BigDecimal getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(BigDecimal moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
    public BigDecimal getMoyennePeriode() { return moyennePeriode; }
    public void setMoyennePeriode(BigDecimal moyennePeriode) { this.moyennePeriode = moyennePeriode; }
    public Integer getRang() { return rang; }
    public void setRang(Integer rang) { this.rang = rang; }
    public Integer getEffectif() { return effectif; }
    public void setEffectif(Integer effectif) { this.effectif = effectif; }
    public BigDecimal getTauxReussite() { return tauxReussite; }
    public void setTauxReussite(BigDecimal tauxReussite) { this.tauxReussite = tauxReussite; }
    public Integer getNbAbsences() { return nbAbsences; }
    public void setNbAbsences(Integer nbAbsences) { this.nbAbsences = nbAbsences; }
    public DecisionType getDecision() { return decision; }
    public void setDecision(DecisionType decision) { this.decision = decision; }
    public String getMention() { return mention; }
    public void setMention(String mention) { this.mention = mention; }
    public String getSeuilApplique() { return seuilApplique; }
    public void setSeuilApplique(String seuilApplique) { this.seuilApplique = seuilApplique; }
    public DecisionCode getCr() { return cr; }
    public void setCr(DecisionCode cr) { this.cr = cr; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDate getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDate dateDecision) { this.dateDecision = dateDecision; }
    public boolean isNotifie() { return notifie; }
    public void setNotifie(boolean notifie) { this.notifie = notifie; }
    public Instant getDateNotification() { return dateNotification; }
    public void setDateNotification(Instant dateNotification) { this.dateNotification = dateNotification; }
}
