package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "note_examen", schema = "pedagogie")
public class NoteExamenEntity {

    @Id
    private UUID id;

    @Column(name = "epreuve_id", nullable = false)
    private UUID epreuveId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    private BigDecimal valeur;

    @Column(nullable = false)
    private String statut;

    @Column(nullable = false)
    private boolean absent;

    @Column(name = "hors_moyenne", nullable = false)
    private boolean horsMoyenne;

    @Column(nullable = false)
    private boolean exclue;

    @Column(name = "motif_exclusion")
    private String motifExclusion;

    @Column(name = "periode_imputation")
    private UUID periodeImputation;

    private UUID correcteur;

    @Column(name = "seconde_correction", nullable = false)
    private boolean secondeCorrection;

    private BigDecimal coefficient;

    @Column(name = "saisi_le")
    private Instant saisiLe;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }
    public boolean isHorsMoyenne() { return horsMoyenne; }
    public void setHorsMoyenne(boolean horsMoyenne) { this.horsMoyenne = horsMoyenne; }
    public boolean isExclue() { return exclue; }
    public void setExclue(boolean exclue) { this.exclue = exclue; }
    public String getMotifExclusion() { return motifExclusion; }
    public void setMotifExclusion(String motifExclusion) { this.motifExclusion = motifExclusion; }
    public UUID getPeriodeImputation() { return periodeImputation; }
    public void setPeriodeImputation(UUID periodeImputation) { this.periodeImputation = periodeImputation; }
    public UUID getCorrecteur() { return correcteur; }
    public void setCorrecteur(UUID correcteur) { this.correcteur = correcteur; }
    public boolean isSecondeCorrection() { return secondeCorrection; }
    public void setSecondeCorrection(boolean secondeCorrection) { this.secondeCorrection = secondeCorrection; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public Instant getSaisiLe() { return saisiLe; }
    public void setSaisiLe(Instant saisiLe) { this.saisiLe = saisiLe; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}