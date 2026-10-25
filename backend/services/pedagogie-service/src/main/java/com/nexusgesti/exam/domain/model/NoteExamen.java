package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public class NoteExamen {

    private UUID id;
    private UUID epreuveId;
    private UUID eleveId;
    private BigDecimal valeur;
    private StatutNoteExamen statut = StatutNoteExamen.PROVISOIRE;
    private boolean absent;
    private boolean horsMoyenne;
    private boolean exclue;
    private String motifExclusion;
    private UUID periodeImputation;
    private UUID correcteur;
    private boolean secondeCorrection;
    private BigDecimal coefficient;
    private Instant saisiLe;

    public NoteExamen saisir(BigDecimal valeur, UUID correcteur) {
        if (exclue) {
            throw ExamException.etatInvalide("Note exclue, saisie impossible");
        }
        this.valeur = valeur;
        this.correcteur = correcteur;
        this.statut = StatutNoteExamen.PROVISOIRE;
        this.saisiLe = Instant.now();
        return this;
    }

    public NoteExamen marquerAbsent(UUID correcteur) {
        this.absent = true;
        this.valeur = null;
        this.correcteur = correcteur;
        this.saisiLe = Instant.now();
        return this;
    }

    public boolean estDefinitive() {
        return statut == StatutNoteExamen.DEFINITIVE;
    }

    public BigDecimal getValeurNormalisee() {
        if (valeur == null) {
            return null;
        }
        return valeur.setScale(2, RoundingMode.HALF_UP);
    }

    public NoteExamen exclure(String motif) {
        if (motif == null || motif.isBlank()) {
            throw ExamException.invalide("Le motif d exclusion est obligatoire");
        }
        this.exclue = true;
        this.motifExclusion = motif;
        return this;
    }

    public NoteExamen contester(String motif) {
        if (motif == null || motif.isBlank()) {
            throw ExamException.invalide("Le motif de contestation est obligatoire");
        }
        this.statut = StatutNoteExamen.CONTESTEE;
        this.motifExclusion = motif;
        return this;
    }

    public NoteExamen valider() {
        if (valeur == null && !absent) {
            throw ExamException.etatInvalide("Aucune valeur a valider");
        }
        this.statut = StatutNoteExamen.DEFINITIVE;
        return this;
    }

    public boolean comptePourMoyenne() {
        return !exclue && !absent && !horsMoyenne;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public StatutNoteExamen getStatut() { return statut; }
    public void setStatut(StatutNoteExamen statut) { this.statut = statut; }
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
}
