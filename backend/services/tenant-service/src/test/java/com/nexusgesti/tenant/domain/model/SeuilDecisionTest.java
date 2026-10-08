package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeuilDecisionTest {

    @Test
    void borneMoyenne() {
        SeuilDecision s = new SeuilDecision();
        s.setMoyenneMinimale(BigDecimal.valueOf(10));
        s.setMoyenneMaximale(BigDecimal.valueOf(14));
        assertTrue(s.contient(BigDecimal.valueOf(12), null, null, null));
        assertFalse(s.contient(BigDecimal.valueOf(9), null, null, null));
        assertFalse(s.contient(BigDecimal.valueOf(15), null, null, null));
    }

    @Test
    void cumulDesContraintes() {
        SeuilDecision s = new SeuilDecision();
        s.setTauxReussiteMinimale(BigDecimal.valueOf(60));
        s.setAbsencesMaximales(5);
        assertTrue(s.contient(null, BigDecimal.valueOf(80), 3, null));
        assertFalse(s.contient(null, BigDecimal.valueOf(59), 3, null));
        assertFalse(s.contient(null, BigDecimal.valueOf(80), 6, null));
    }

    @Test
    void rangMinEtMaxAppliques() {
        SeuilDecision s = new SeuilDecision();
        s.setRangMin(2);
        s.setRangMax(5);
        assertTrue(s.contient(null, null, null, 3));
        assertFalse(s.contient(null, null, null, 1));
        assertFalse(s.contient(null, null, null, 6));
    }

    @Test
    void borneBassePrioriseMoyenne() {
        SeuilDecision s = new SeuilDecision();
        s.setMoyenneMinimale(BigDecimal.valueOf(10));
        s.setRangMin(3);
        assertEquals("10", s.getBorneBasse());
    }
}