package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Note {

    private UUID id;
    private UUID evaluationId;
    private UUID feuilleId;
    private UUID eleveId;
    private BigDecimal valeur;
    private StatutNote statut;
    private boolean absent;
    private boolean exclue;
    private String motifExclusion;
    private BigDecimal coefficientReel;
    private TypeSourceNote source;
    private UUID saisiePar;
    private Instant saisieLe;
    private boolean approuvee;
    private String motifContestation;
    private Instant dateContestation;
    private UUID remplaceePar;

    public Note() {
        this.statut = StatutNote.PROVISOIRE;
        this.source = TypeSourceNote.EVALUATION_CONTINUE;
    }

    public Note saisir(BigDecimal valeur, UUID par, BigDecimal noteMax) {
        if (absent) {
            throw AssessmentException.etatInvalide("Une note deja marquee absente ne peut plus etre saisie");
        }
        if (valeur == null) {
            throw AssessmentException.invalide("La valeur de la note est obligatoire");
        }
        if (noteMax != null && valeur.compareTo(noteMax) > 0) {
            throw AssessmentException.invalide("La note depasse le maximum du bareme : " + noteMax);
        }
        this.valeur = valeur;
        this.statut = StatutNote.PROVISOIRE;
        this.saisiePar = par;
        this.saisieLe = Instant.now();
        return this;
    }

    public Note marquerAbsent(UUID par) {
        this.absent = true;
        this.valeur = null;
        this.statut = StatutNote.PROVISOIRE;
        this.saisiePar = par;
        this.saisieLe = Instant.now();
        return this;
    }

    public Note exclure(String motif) {
        if (motif == null || motif.isBlank()) {
            throw AssessmentException.invalide("Le motif d exclusion est obligatoire");
        }
        this.exclue = true;
        this.motifExclusion = motif;
        this.statut = StatutNote.EXCLUE;
        return this;
    }

    public Note contester(String motif) {
        if (motif == null || motif.isBlank()) {
            throw AssessmentException.invalide("Le motif de contestation est obligatoire");
        }
        if (valeur == null) {
            throw AssessmentException.etatInvalide("Seule une note saisie peut etre contestee");
        }
        if (statut == StatutNote.EXCLUE) {
            throw AssessmentException.etatInvalide("Une note exclue ne peut pas etre contestee");
        }
        this.motifContestation = motif;
        this.dateContestation = Instant.now();
        this.statut = StatutNote.CONTESTEE;
        return this;
    }

    public Note definir() {
        if (valeur == null) {
            throw AssessmentException.etatInvalide("Impossible de rendre definitive une note non saisie");
        }
        this.statut = StatutNote.DEFINITIVE;
        return this;
    }

    public boolean estDefinitive() {
        return statut == StatutNote.DEFINITIVE;
    }

    public BigDecimal getValeurEffective() {
        if (absent || exclue) {
            return null;
        }
        return valeur;
    }

    public boolean estAbsente() {
        return absent;
    }

    public boolean comptePourMoyenne() {
        return !exclue && !absent && valeur != null;
    }

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
    public StatutNote getStatut() { return statut; }
    public void setStatut(StatutNote statut) { this.statut = statut; }
    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }
    public boolean isExclue() { return exclue; }
    public void setExclue(boolean exclue) { this.exclue = exclue; }
    public String getMotifExclusion() { return motifExclusion; }
    public void setMotifExclusion(String motifExclusion) { this.motifExclusion = motifExclusion; }
    public BigDecimal getCoefficientReel() { return coefficientReel; }
    public void setCoefficientReel(BigDecimal coefficientReel) { this.coefficientReel = coefficientReel; }
    public TypeSourceNote getSource() { return source; }
    public void setSource(TypeSourceNote source) { this.source = source; }
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
}
