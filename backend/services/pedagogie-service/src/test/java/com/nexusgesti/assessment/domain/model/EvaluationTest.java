package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluationTest {

    private Evaluation evaluation(StatutEvaluation statut) {
        Evaluation e = new Evaluation();
        e.setIdEvaluation(UUID.randomUUID());
        e.setStatut(statut);
        e.setNoteMax(new BigDecimal("20"));
        return e;
    }

    @Test
    void ouvrirDepuisBrouillonGenereLaFeuille() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        assertEquals(StatutEvaluation.OUVERTE, e.getStatut());
        assertNotNull(e.getFeuille());
    }

    @Test
    void ouvrirDepuisAnnuleeRefuse() {
        Evaluation e = evaluation(StatutEvaluation.ANNULEE);
        AssessmentException ex = assertThrows(AssessmentException.class, e::ouvrir);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void saisieSansFeuilleRefusee() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON);
        AssessmentException ex = assertThrows(AssessmentException.class,
                () -> e.saisirNote(UUID.randomUUID(), new BigDecimal("12"), UUID.randomUUID()));
        assertEquals("ETAT_INVALIDE", ex.getCode());
    }

    @Test
    void saisieNotePuisClotureRendDefinitive() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        UUID eleve = UUID.randomUUID();
        e.saisirNote(eleve, new BigDecimal("12"), UUID.randomUUID());
        assertEquals(1, e.getFeuille().getNbNotesSaisies());
        e.cloturer();
        assertTrue(e.getNote(eleve).orElseThrow().estDefinitive());
        assertEquals(StatutEvaluation.CLOTUREE, e.getStatut());
    }

    @Test
    void noteAuDelaDuMaximumRefusee() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        AssessmentException ex = assertThrows(AssessmentException.class,
                () -> e.saisirNote(UUID.randomUUID(), new BigDecimal("21"), UUID.randomUUID()));
        assertEquals("INVALIDE", ex.getCode());
    }

    @Test
    void marquerAbsentIncrementeLeCompteur() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        UUID eleve = UUID.randomUUID();
        e.marquerAbsent(eleve, UUID.randomUUID());
        assertTrue(e.getNote(eleve).orElseThrow().estAbsente());
        assertEquals(1, e.getFeuille().getNbAbsents());
    }

    @Test
    void publierApresClotureSeulement() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        assertThrows(AssessmentException.class, e::publier);
        e.cloturer().publier();
        assertEquals(StatutEvaluation.PUBLIEE, e.getStatut());
        assertTrue(e.estClosePourSaisie());
    }

    @Test
    void annulationEnregistreLeMotifEtInterditSansMotif() {
        Evaluation e = evaluation(StatutEvaluation.BROUILLON).ouvrir();
        assertThrows(AssessmentException.class, () -> e.annuler(" "));
        e.annuler("erreur de sujet");
        assertEquals(StatutEvaluation.ANNULEE, e.getStatut());
        assertEquals("erreur de sujet", e.getMotifAnnulation());
    }
}
