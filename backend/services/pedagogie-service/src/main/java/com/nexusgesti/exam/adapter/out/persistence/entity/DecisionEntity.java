package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "decision", schema = "pedagogie")
public class DecisionEntity {

    @Id
    private UUID id;

    @Column(name = "deliberation_id", nullable = false)
    private UUID deliberationId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "moyenne_generale")
    private BigDecimal moyenneGenerale;

    @Column(name = "moyenne_periode")
    private BigDecimal moyennePeriode;

    private Integer rang;

    private Integer effectif;

    @Column(name = "taux_reussite")
    private BigDecimal tauxReussite;

    @Column(name = "nb_absences")
    private Integer nbAbsences;

    @Column(nullable = false)
    private String decision;

    private String mention;

    @Column(name = "seuil_applique")
    private String seuilApplique;

    private String cr;

    private String motif;

    @Column(name = "date_decision")
    private LocalDate dateDecision;

    @Column(nullable = false)
    private boolean notifie;

    @Column(name = "date_notification")
    private Instant dateNotification;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getMention() { return mention; }
    public void setMention(String mention) { this.mention = mention; }
    public String getSeuilApplique() { return seuilApplique; }
    public void setSeuilApplique(String seuilApplique) { this.seuilApplique = seuilApplique; }
    public String getCr() { return cr; }
    public void setCr(String cr) { this.cr = cr; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDate getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDate dateDecision) { this.dateDecision = dateDecision; }
    public boolean isNotifie() { return notifie; }
    public void setNotifie(boolean notifie) { this.notifie = notifie; }
    public Instant getDateNotification() { return dateNotification; }
    public void setDateNotification(Instant dateNotification) { this.dateNotification = dateNotification; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}