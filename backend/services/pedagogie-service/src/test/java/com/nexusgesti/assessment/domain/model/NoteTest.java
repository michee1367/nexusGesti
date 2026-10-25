package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteTest {

    @Test
    void saisiePuisDefinitive() {
        Note n = new Note();
        n.saisir(new BigDecimal("14"), null, new BigDecimal("20"));
        assertEquals(new BigDecimal("14"), n.getValeurEffective());
        assertTrue(n.comptePourMoyenne());
        n.definir();
        assertTrue(n.estDefinitive());
    }

    @Test
    void valeurObligatoire() {
        Note n = new Note();
        assertThrows(AssessmentException.class, () -> n.saisir(null, null, new BigDecimal("20")));
    }

    @Test
    void absenceNeComptePasDansLaMoyenne() {
        Note n = new Note();
        n.saisir(new BigDecimal("14"), null, new BigDecimal("20"));
        n.marquerAbsent(null);
        assertTrue(n.estAbsente());
        assertNull(n.getValeurEffective());
        assertFalse(n.comptePourMoyenne());
    }

    @Test
    void exclusionExigeUnMotif() {
        Note n = new Note();
        n.saisir(new BigDecimal("14"), null, new BigDecimal("20"));
        assertThrows(AssessmentException.class, () -> n.exclure(""));
        n.exclure("fraude");
        assertFalse(n.comptePourMoyenne());
    }

    @Test
    void contestationExigeUneNoteSaisie() {
        Note n = new Note();
        assertThrows(AssessmentException.class, () -> n.contester("doute"));
        n.saisir(new BigDecimal("14"), null, new BigDecimal("20"));
        n.contester("doute");
        assertEquals(StatutNote.CONTESTEE, n.getStatut());
    }
}
