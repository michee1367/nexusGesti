package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public class FeuilleNote {

    private UUID id;
    private UUID evaluationId;
    private StatutBulletin statut;
    private int nbNotesAttendues;
    private int nbNotesSaisies;
    private int nbAbsents;
    private Instant ouverteLe;
    private Instant clotureeLe;
    private boolean verrouillee;

    public FeuilleNote ouvrir() {
        if (statut == null) {
            statut = StatutBulletin.BROUILLON;
        }
        if (ouverteLe == null) {
            ouverteLe = Instant.now();
        }
        return this;
    }

    public FeuilleNote verrouiller() {
        verrouillee = true;
        clotureeLe = Instant.now();
        return this;
    }

    public FeuilleNote enregistrerSaisie() {
        verifierModifiable();
        nbNotesSaisies++;
        return this;
    }

    public FeuilleNote enregistrerAbsence() {
        verifierModifiable();
        nbAbsents++;
        return this;
    }

    public void verifierModifiable() {
        if (verrouillee) {
            throw AssessmentException.etatInvalide("Feuille de notes verrouillee");
        }
    }

    public BigDecimal getProgression() {
        if (nbNotesAttendues <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        int points = nbNotesSaisies + nbAbsents;
        return BigDecimal.valueOf(points)
                .divide(BigDecimal.valueOf(nbNotesAttendues), 4, RoundingMode.HALF_UP);
    }

    public boolean estComplete() {
        return nbNotesAttendues > 0 && (nbNotesSaisies + nbAbsents) >= nbNotesAttendues;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEvaluationId() { return evaluationId; }
    public void setEvaluationId(UUID evaluationId) { this.evaluationId = evaluationId; }
    public StatutBulletin getStatut() { return statut; }
    public void setStatut(StatutBulletin statut) { this.statut = statut; }
    public int getNbNotesAttendues() { return nbNotesAttendues; }
    public void setNbNotesAttendues(int nbNotesAttendues) { this.nbNotesAttendues = nbNotesAttendues; }
    public int getNbNotesSaisies() { return nbNotesSaisies; }
    public void setNbNotesSaisies(int nbNotesSaisies) { this.nbNotesSaisies = nbNotesSaisies; }
    public int getNbAbsents() { return nbAbsents; }
    public void setNbAbsents(int nbAbsents) { this.nbAbsents = nbAbsents; }
    public Instant getOuverteLe() { return ouverteLe; }
    public void setOuverteLe(Instant ouverteLe) { this.ouverteLe = ouverteLe; }
    public Instant getClotureeLe() { return clotureeLe; }
    public void setClotureeLe(Instant clotureeLe) { this.clotureeLe = clotureeLe; }
    public boolean isVerrouillee() { return verrouillee; }
    public void setVerrouillee(boolean verrouillee) { this.verrouillee = verrouillee; }
}
