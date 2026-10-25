package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "note", schema = "pedagogie")
public class NoteEntity {

    @Id
    private UUID id;

    @Column(name = "evaluation_id", nullable = false)
    private UUID evaluationId;

    @Column(name = "feuille_id")
    private UUID feuilleId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    private BigDecimal valeur;

    @Column(nullable = false)
    private String statut;

    @Column(nullable = false)
    private boolean absent;

    @Column(nullable = false)
    private boolean exclue;

    @Column(name = "motif_exclusion")
    private String motifExclusion;

    @Column(name = "coefficient_reel")
    private BigDecimal coefficientReel;

    private String source;

    @Column(name = "saisie_par")
    private UUID saisiePar;

    @Column(name = "saisie_le")
    private Instant saisieLe;

    @Column(nullable = false)
    private boolean approuvee;

    @Column(name = "motif_contestation")
    private String motifContestation;

    @Column(name = "date_contestation")
    private Instant dateContestation;

    @Column(name = "remplacee_par")
    private UUID remplaceePar;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEvaluationId() { return evaluationId; }
    public void setEvaluationId(UUID evaluationId) { this.evaluationId = evaluationId; }
    public UUID getFeuilleId() { return feuilleId; }
    public void setFeuilleId(UUID feuilleId) { this.feuilleId = feuilleId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }
    public boolean isExclue() { return exclue; }
    public void setExclue(boolean exclue) { this.exclue = exclue; }
    public String getMotifExclusion() { return motifExclusion; }
    public void setMotifExclusion(String motifExclusion) { this.motifExclusion = motifExclusion; }
    public BigDecimal getCoefficientReel() { return coefficientReel; }
    public void setCoefficientReel(BigDecimal coefficientReel) { this.coefficientReel = coefficientReel; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public UUID getSaisiePar() { return saisiePar; }
    public void setSaisiePar(UUID saisiePar) { this.saisiePar = saisiePar; }
    public Instant getSaisieLe() { return saisieLe; }
    public void setSaisieLe(Instant saisieLe) { this.saisieLe = saisieLe; }
    public boolean isApprouvee() { return approuvee; }
    public void setApprouvee(boolean approuvee) { this.approuvee = approuvee; }
    public String getMotifContestation() { return motifContestation; }
    public void setMotifContestation(String motifContestation) { this.motifContestation = motifContestation; }
    public Instant getDateContestation() { return dateContestation; }
    public void setDateContestation(Instant dateContestation) { this.dateContestation = dateContestation; }
    public UUID getRemplaceePar() { return remplaceePar; }
    public void setRemplaceePar(UUID remplaceePar) { this.remplaceePar = remplaceePar; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
