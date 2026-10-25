package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeanceTest {

    private Seance seance(StatutSeance statut) {
        Seance s = new Seance();
        s.setStatut(statut);
        return s;
    }

    @Test
    void demarrageDepuisPlanifiee() {
        Seance s = seance(StatutSeance.PLANIFIEE).demarrer();
        assertEquals(StatutSeance.EN_COURS, s.getStatut());
    }

    @Test
    void demarrageDepuisAnnuleeRefuse() {
        Seance s = seance(StatutSeance.ANNULEE);
        AttendanceException ex = assertThrows(AttendanceException.class, s::demarrer);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void clotureDepuisEnCours() {
        Seance s = seance(StatutSeance.EN_COURS).cloturer();
        assertEquals(StatutSeance.TERMINEE, s.getStatut());
    }

    @Test
    void clotureDepuisPlanifieeRefusee() {
        Seance s = seance(StatutSeance.PLANIFIEE);
        assertThrows(AttendanceException.class, s::cloturer);
    }

    @Test
    void reportDepuisTermineeRefuse() {
        Seance s = seance(StatutSeance.TERMINEE);
        assertThrows(AttendanceException.class, s::reporter);
    }

    @Test
    void annulationEnregistreLeMotif() {
        Seance s = seance(StatutSeance.PLANIFIEE).annuler("enseignant indisponible");
        assertEquals(StatutSeance.ANNULEE, s.getStatut());
        assertTrue(s.isAnnulee());
        assertEquals("enseignant indisponible", s.getMotifAnnulation());
    }

    @Test
    void annulationDepuisTermineeRefusee() {
        Seance s = seance(StatutSeance.TERMINEE);
        assertThrows(AttendanceException.class, () -> s.annuler("trop tard"));
    }

    @Test
    void dureeReelleCalculee() {
        Seance s = new Seance();
        s.setHeureDebut(java.time.LocalTime.of(8, 0));
        s.setHeureFin(java.time.LocalTime.of(9, 30));
        assertNotNull(s.getDureeReelle());
        assertEquals(90, s.getDureeReelle().toMinutes());
    }
}
