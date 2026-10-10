package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JustificatifTest {

    @Test
    void depotPositionneStatutEtDate() {
        Justificatif j = new Justificatif().deposer();
        assertEquals(StatutJustificatif.EN_ATTENTE, j.getStatut());
        assertNotNull(j.getDateDepot());
    }

    @Test
    void acceptationDepuisEnAttente() {
        Justificatif j = new Justificatif().deposer();
        UUID par = UUID.randomUUID();
        j.accepter(par);
        assertEquals(StatutJustificatif.ACCEPTE, j.getStatut());
        assertEquals(par, j.getTraitePar());
        assertNotNull(j.getTraiteLe());
    }

    @Test
    void acceptationDepuisAccepteRefusee() {
        Justificatif j = new Justificatif().deposer();
        j.accepter(UUID.randomUUID());
        AttendanceException ex = assertThrows(AttendanceException.class, () -> j.accepter(UUID.randomUUID()));
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void rejetEnregistreLeMotif() {
        Justificatif j = new Justificatif().deposer();
        j.rejeter("piece illisible", UUID.randomUUID());
        assertEquals(StatutJustificatif.REJETE, j.getStatut());
        assertEquals("piece illisible", j.getMotifRejet());
    }

    @Test
    void rejetDepuisRejeteRefuse() {
        Justificatif j = new Justificatif().deposer();
        j.rejeter("motif", UUID.randomUUID());
        assertThrows(AttendanceException.class, () -> j.rejeter("autre", UUID.randomUUID()));
    }

    @Test
    void dateReferenceConservee() {
        Justificatif j = new Justificatif().deposer();
        LocalDate d = LocalDate.now().minusDays(3);
        j.setDateReference(d);
        assertEquals(d, j.getDateReference());
    }
}
