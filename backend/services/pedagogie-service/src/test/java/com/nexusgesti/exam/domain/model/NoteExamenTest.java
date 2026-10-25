package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteExamenTest {

    @Test
    void saisirPositionneValeurEtStatut() {
        NoteExamen note = new NoteExamen();
        UUID correcteur = UUID.randomUUID();
        note.saisir(new BigDecimal("12.5"), correcteur);
        assertEquals(new BigDecimal("12.5"), note.getValeur());
        assertEquals(StatutNoteExamen.PROVISOIRE, note.getStatut());
        assertEquals(correcteur, note.getCorrecteur());
        assertNotNull(note.getSaisiLe());
    }

    @Test
    void marquerAbsentAnnuleValeur() {
        NoteExamen note = new NoteExamen();
        note.marquerAbsent(null);
        assertTrue(note.isAbsent());
        assertNull(note.getValeur());
    }

    @Test
    void exclureExigeUnMotif() {
        NoteExamen note = new NoteExamen();
        assertThrows(ExamException.class, () -> note.exclure(null));
        note.exclure("fraude");
        assertTrue(note.isExclue());
        assertEquals("fraude", note.getMotifExclusion());
    }

    @Test
    void saisirSurNoteExclueEstRefuse() {
        NoteExamen note = new NoteExamen();
        note.exclure("fraude");
        assertThrows(ExamException.class, () -> note.saisir(new BigDecimal("10"), null));
    }

    @Test
    void validerExigeUneValeurOuAbsence() {
        NoteExamen note = new NoteExamen();
        assertThrows(ExamException.class, note::valider);
        note.saisir(new BigDecimal("10"), null);
        note.valider();
        assertTrue(note.estDefinitive());
    }

    @Test
    void comptePourMoyenneSelonLesDrapeaux() {
        NoteExamen note = new NoteExamen();
        note.saisir(new BigDecimal("10"), null);
        assertTrue(note.comptePourMoyenne());
        note.setHorsMoyenne(true);
        assertFalse(note.comptePourMoyenne());
    }

    @Test
    void valeurNormaliseeDeuxDecimales() {
        NoteExamen note = new NoteExamen();
        note.setValeur(new BigDecimal("12.345"));
        assertEquals(new BigDecimal("12.35"), note.getValeurNormalisee());
    }
}
