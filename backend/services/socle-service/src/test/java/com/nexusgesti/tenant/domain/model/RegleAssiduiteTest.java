package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegleAssiduiteTest {

    @Test
    void plafondAbsencesEnPeriode() {
        RegleAssiduite r = new RegleAssiduite();
        r.setAbsencesMaxParPeriode(3);
        assertTrue(r.estRespectee(2, 40));
        assertFalse(r.estRespectee(4, 40));
    }

    @Test
    void tauxDePresenceMinimal() {
        RegleAssiduite r = new RegleAssiduite();
        r.setTauxPresenceMinimale(new BigDecimal("80.00"));
        assertTrue(r.estRespectee(2, 10));
        assertFalse(r.estRespectee(4, 10));
    }

    @Test
    void tauxPresenceCalcule() {
        RegleAssiduite r = new RegleAssiduite();
        assertEquals(0, r.getTauxPresence(0, 0).compareTo(new BigDecimal("0")));
        assertEquals(0, r.getTauxPresence(5, 5).compareTo(new BigDecimal("0")));
        assertEquals(0, r.getTauxPresence(12, 3).compareTo(new BigDecimal("75.00")));
    }
}