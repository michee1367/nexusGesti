package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Evaluation {

    private UUID idEvaluation;
    private String reference;
    private String libelle;
    private TypeEvaluation type;
    private UUID classeId;
    private UUID coursId;
    private UUID ueId;
    private UUID semestreId;
    private UUID matiereId;
    private UUID enseignantId;
    private UUID anneeId;
    private UUID periodeId;
    private LocalDate date;
    private LocalTime heureDebut;
    private Integer duree;
    private UUID salleId;
    private StatutEvaluation statut;
    private UUID grilleId;
    private Integer grilleVersion;
    private String baremeType;
    private BigDecimal noteMax;
    private BigDecimal coefficientDeclare;
    private String consignes;
    private boolean anonymise;
    private boolean compteeDansMoyenne = true;
    private boolean declencheSession;
    private Instant annuleLe;
    private String motifAnnulation;
    private Instant creeLe;

    private FeuilleNote feuille;
    private List<Note> notes = new ArrayList<>();

    public Evaluation ouvrir() {
        if (statut != StatutEvaluation.BROUILLON && statut != StatutEvaluation.PLANIFIEE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutEvaluation.OUVERTE.name());
        }
        this.statut = StatutEvaluation.OUVERTE;
        if (feuille == null) {
            genererFeuille();
        } else {
            feuille.ouvrir();
        }
        return this;
    }

    public FeuilleNote genererFeuille() {
        if (statut == StatutEvaluation.ANNULEE || statut == StatutEvaluation.PUBLIEE) {
            throw AssessmentException.etatInvalide(
                    "Aucune feuille de notes possible pour une evaluation " + statut);
        }
        if (feuille == null) {
            feuille = new FeuilleNote();
            feuille.setId(UUID.randomUUID());
            feuille.setEvaluationId(idEvaluation);
        }
        feuille.ouvrir();
        if (statut == null || statut == StatutEvaluation.BROUILLON || statut == StatutEvaluation.PLANIFIEE) {
            this.statut = StatutEvaluation.OUVERTE;
        }
        return feuille;
    }

    public Evaluation saisirNote(UUID eleve, BigDecimal valeur, UUID par) {
        verifierSaisieOuverte();
        Note note = notes.stream()
                .filter(n -> eleve.equals(n.getEleveId()))
                .findFirst()
                .orElseGet(() -> creerNote(eleve));
        boolean nouvelle = note.getValeur() == null && !note.isAbsent();
        note.saisir(valeur, par, noteMax);
        if (nouvelle) {
            feuille.enregistrerSaisie();
        }
        return this;
    }

    public Evaluation marquerAbsent(UUID eleve, UUID par) {
        verifierSaisieOuverte();
        Note note = notes.stream()
                .filter(n -> eleve.equals(n.getEleveId()))
                .findFirst()
                .orElseGet(() -> creerNote(eleve));
        boolean nouvelle = note.getValeur() == null && !note.isAbsent();
        note.marquerAbsent(par);
        if (nouvelle) {
            feuille.enregistrerAbsence();
        }
        return this;
    }

    public Evaluation cloturer() {
        if (statut != StatutEvaluation.OUVERTE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutEvaluation.CLOTUREE.name());
        }
        this.statut = StatutEvaluation.CLOTUREE;
        if (feuille != null) {
            feuille.verrouiller();
        }
        notes.forEach(n -> {
            if (n.getValeur() != null && !n.isExclue()) {
                n.definir();
            }
        });
        return this;
    }

    public Evaluation publier() {
        if (statut != StatutEvaluation.CLOTUREE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutEvaluation.PUBLIEE.name());
        }
        this.statut = StatutEvaluation.PUBLIEE;
        return this;
    }

    public Evaluation annuler(String motif) {
        if (statut == StatutEvaluation.PUBLIEE) {
            throw AssessmentException.transitionInvalide(statut.name(), StatutEvaluation.ANNULEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw AssessmentException.invalide("Le motif d annulation est obligatoire");
        }
        this.statut = StatutEvaluation.ANNULEE;
        this.annuleLe = Instant.now();
        this.motifAnnulation = motif;
        if (feuille != null) {
            feuille.verrouiller();
        }
        return this;
    }

    public boolean estClosePourSaisie() {
        return statut == StatutEvaluation.CLOTUREE
                || statut == StatutEvaluation.PUBLIEE
                || statut == StatutEvaluation.ANNULEE;
    }

    public BigDecimal getMaxNoteAtteignable() {
        return noteMax;
    }

    public Optional<Note> getNote(UUID eleve) {
        return notes.stream().filter(n -> eleve.equals(n.getEleveId())).findFirst();
    }

    private void verifierSaisieOuverte() {
        if (estClosePourSaisie()) {
            throw AssessmentException.etatInvalide("Saisie fermee pour une evaluation " + statut);
        }
        if (feuille == null) {
            throw AssessmentException.etatInvalide("Aucune feuille de notes pour cette evaluation");
        }
        feuille.verifierModifiable();
    }

    private Note creerNote(UUID eleve) {
        Note note = new Note();
        note.setId(UUID.randomUUID());
        note.setEvaluationId(idEvaluation);
        note.setFeuilleId(feuille == null ? null : feuille.getId());
        note.setEleveId(eleve);
        notes.add(note);
        return note;
    }

    public UUID getIdEvaluation() { return idEvaluation; }
    public void setIdEvaluation(UUID idEvaluation) { this.idEvaluation = idEvaluation; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeEvaluation getType() { return type; }
    public void setType(TypeEvaluation type) { this.type = type; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public StatutEvaluation getStatut() { return statut; }
    public void setStatut(StatutEvaluation statut) { this.statut = statut; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public Integer getGrilleVersion() { return grilleVersion; }
    public void setGrilleVersion(Integer grilleVersion) { this.grilleVersion = grilleVersion; }
    public String getBaremeType() { return baremeType; }
    public void setBaremeType(String baremeType) { this.baremeType = baremeType; }
    public BigDecimal getNoteMax() { return noteMax; }
    public void setNoteMax(BigDecimal noteMax) { this.noteMax = noteMax; }
    public BigDecimal getCoefficientDeclare() { return coefficientDeclare; }
    public void setCoefficientDeclare(BigDecimal coefficientDeclare) { this.coefficientDeclare = coefficientDeclare; }
    public String getConsignes() { return consignes; }
    public void setConsignes(String consignes) { this.consignes = consignes; }
    public boolean isAnonymise() { return anonymise; }
    public void setAnonymise(boolean anonymise) { this.anonymise = anonymise; }
    public boolean isCompteeDansMoyenne() { return compteeDansMoyenne; }
    public void setCompteeDansMoyenne(boolean compteeDansMoyenne) { this.compteeDansMoyenne = compteeDansMoyenne; }
    public boolean isDeclencheSession() { return declencheSession; }
    public void setDeclencheSession(boolean declencheSession) { this.declencheSession = declencheSession; }
    public Instant getAnnuleLe() { return annuleLe; }
    public void setAnnuleLe(Instant annuleLe) { this.annuleLe = annuleLe; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public Instant getCreeLe() { return creeLe; }
    public void setCreeLe(Instant creeLe) { this.creeLe = creeLe; }
    public FeuilleNote getFeuille() { return feuille; }
    public void setFeuille(FeuilleNote feuille) { this.feuille = feuille; }
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes == null ? new ArrayList<>() : new ArrayList<>(notes); }
}
