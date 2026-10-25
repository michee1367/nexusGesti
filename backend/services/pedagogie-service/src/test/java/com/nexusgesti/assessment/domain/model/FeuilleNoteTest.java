package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeuilleNoteTest {

    @Test
    void ouverturePositionneBrouillon() {
        FeuilleNote f = new FeuilleNote().ouvrir();
        assertEquals(StatutBulletin.BROUILLON, f.getStatut());
    }

    @Test
    void progressionEtCompletion() {
        FeuilleNote f = new FeuilleNote().ouvrir();
        f.setNbNotesAttendues(4);
        f.enregistrerSaisie();
        f.enregistrerSaisie();
        f.enregistrerAbsence();
        assertEquals(new BigDecimal("0.7500"), f.getProgression());
        assertFalse(f.estComplete());
        f.enregistrerSaisie();
        assertTrue(f.estComplete());
    }

    @Test
    void feuilleVerrouilleeRefuseLaSaisie() {
        FeuilleNote f = new FeuilleNote().ouvrir();
        f.verrouiller();
        assertThrows(AssessmentException.class, f::enregistrerSaisie);
    }

    @Test
    void progressionNulleSansAttendu() {
        FeuilleNote f = new FeuilleNote().ouvrir();
        assertEquals(new BigDecimal("0.0000"), f.getProgression());
    }
}
