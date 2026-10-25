package com.nexusgesti.attendance.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuiviAssiduiteTest {

    private SuiviAssiduite suivi(int tenues, int justifiees, int injustifiees) {
        SuiviAssiduite s = new SuiviAssiduite();
        s.setNbSeancesTenues(tenues);
        s.setNbAbsencesJustifiees(justifiees);
        s.setNbAbsencesInjustifiees(injustifiees);
        return s;
    }

    @Test
    void tauxPresenceCalcule() {
        SuiviAssiduite s = suivi(10, 0, 0).recalculer();
        assertEquals(new BigDecimal("100.00"), s.getTauxPresence());
    }

    @Test
    void tauxPresenceAvecAbsences() {
        SuiviAssiduite s = suivi(10, 1, 1).recalculer();
        assertEquals(new BigDecimal("80.00"), s.getTauxPresence());
    }

    @Test
    void tauxSansSeanceEstZero() {
        assertEquals(BigDecimal.ZERO, suivi(0, 0, 0).recalculer().getTauxPresence());
    }

    @Test
    void tauxAbsenceJustifiee() {
        SuiviAssiduite s = suivi(10, 3, 1);
        assertEquals(new BigDecimal("75.00"), s.getTauxAbsenceJustifiee());
    }

    @Test
    void alerteSelonRegle() {
        SuiviAssiduite s = suivi(10, 0, 0);
        assertFalse(s.estEnAlerte());
        s.setRespecteRegle(false);
        assertTrue(s.estEnAlerte());
        s.setRespecteRegle(true);
        assertFalse(s.estEnAlerte());
    }
}
