package com.nexusgesti.assessment.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MoteurCalculMoyenneTest {

    private final MoteurCalculMoyenne moteur = new MoteurCalculMoyenne();

    private Note note(UUID eleve, String valeur) {
        Note n = new Note();
        n.setEleveId(eleve);
        if (valeur != null) {
            n.saisir(new BigDecimal(valeur), null, new BigDecimal("20"));
        }
        return n;
    }

    @Test
    void moyenneIgnoreAbsentsEtExclues() {
        UUID eleve = UUID.randomUUID();
        Note absente = note(eleve, null);
        absente.marquerAbsent(null);
        Note exclue = note(eleve, "5");
        exclue.exclure("fraude");

        ResultatMoyenne r = moteur.calculer(List.of(
                note(eleve, "10"), note(eleve, "20"), absente, exclue));

        assertEquals(new BigDecimal("15.00"), r.getMoyenne());
        assertEquals(new BigDecimal("10"), r.getNoteMin());
        assertEquals(new BigDecimal("20"), r.getNoteMax());
        assertEquals(2, r.getNbNotes());
        assertEquals(1, r.getEffectif());
    }

    @Test
    void moyenneNulleSansNoteValide() {
        ResultatMoyenne r = moteur.calculer(List.of());
        assertNull(r.getMoyenne());
        assertEquals(0, r.getNbNotes());
    }

    @Test
    void classementOrdonneParMoyenneDecroissante() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        List<Classement> classement = moteur.classer(Map.of(
                a, List.of(note(a, "12")),
                b, List.of(note(b, "18")),
                c, List.of(note(c, "15"))));

        assertEquals(3, classement.size());
        assertEquals(b, classement.get(0).getEleveId());
        assertEquals(1, classement.get(0).getRang());
        assertEquals(c, classement.get(1).getEleveId());
        assertEquals(a, classement.get(2).getEleveId());
        assertEquals(3, classement.get(2).getRang());
    }
}
