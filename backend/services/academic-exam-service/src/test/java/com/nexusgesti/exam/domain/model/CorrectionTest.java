package com.nexusgesti.exam.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CorrectionTest {

    @Test
    void calculerStatistiquesSurNotesComptables() {
        Correction correction = new Correction();
        correction.ouvrir();

        NoteExamen a = note("12");
        NoteExamen b = note("18");
        NoteExamen exclue = note("4");
        exclue.exclure("fraude");
        NoteExamen absente = new NoteExamen();
        absente.marquerAbsent(null);

        correction.calculerStatistiques(List.of(a, b, exclue, absente));

        assertEquals(2, correction.getNbNotesCorrigees());
        assertEquals(new BigDecimal("15.00"), correction.getMoyenneBrute());
        assertEquals(new BigDecimal("18"), correction.getPlusForteMoyenne());
        assertEquals(new BigDecimal("12"), correction.getPlusFaibleMoyenne());
        assertEquals(StatutCorrection.EN_COURS, correction.getStatut());
    }

    @Test
    void calculerSansNoteComptableLaisseMoyenneNulle() {
        Correction correction = new Correction();
        correction.ouvrir();
        correction.calculerStatistiques(List.of());
        assertEquals(0, correction.getNbNotesCorrigees());
        assertNull(correction.getMoyenneBrute());
    }

    @Test
    void cloturerDepuisOuverteEstAutorise() {
        Correction correction = new Correction();
        correction.ouvrir();
        correction.cloturer();
        assertEquals(StatutCorrection.CLOTUREE, correction.getStatut());
    }

    @Test
    void moyenneRetenueEgaleMoyenneBruteDoncEcartNonSignificatif() {
        Correction correction = new Correction();
        correction.ouvrir();
        correction.calculerStatistiques(List.of(note("10"), note("20")));
        assertFalse(correction.getEcartSignificatif());
    }

    private NoteExamen note(String valeur) {
        NoteExamen note = new NoteExamen();
        note.setId(UUID.randomUUID());
        note.saisir(new BigDecimal(valeur), null);
        return note;
    }
}
