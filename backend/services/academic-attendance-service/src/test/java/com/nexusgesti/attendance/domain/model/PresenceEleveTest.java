package com.nexusgesti.attendance.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PresenceEleveTest {

    @Test
    void presentEstPresentPasAbsent() {
        PresenceEleve p = new PresenceEleve();
        p.setStatut(StatutPresence.PRESENT);
        assertTrue(p.estPresent());
        assertFalse(p.estAbsent());
    }

    @Test
    void retardEstRetard() {
        PresenceEleve p = new PresenceEleve();
        p.setStatut(StatutPresence.RETARD);
        assertTrue(p.estRetard());
        assertFalse(p.estAbsent());
    }

    @Test
    void minutesRetardParDefautZero() {
        assertEquals(0, new PresenceEleve().getMinutesRetard());
    }

    @Test
    void excuserTransformeAbsenceEnExcuse() {
        PresenceEleve p = new PresenceEleve();
        p.setStatut(StatutPresence.ABSENT_NON_EXCUSE);
        UUID justificatif = UUID.randomUUID();
        p.excuser(justificatif);
        assertEquals(StatutPresence.ABSENT_EXCUSE, p.getStatut());
        assertTrue(p.isJustifiee());
        assertEquals(justificatif, p.getJustificatifId());
    }

    @Test
    void excuserUnPresentNeChangePasLeStatut() {
        PresenceEleve p = new PresenceEleve();
        p.setStatut(StatutPresence.PRESENT);
        p.excuser(UUID.randomUUID());
        assertEquals(StatutPresence.PRESENT, p.getStatut());
        assertTrue(p.isJustifiee());
    }
}
