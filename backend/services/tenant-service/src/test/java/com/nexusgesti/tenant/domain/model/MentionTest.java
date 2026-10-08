package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentionTest {

    @Test
    void contientRespecteLesBornes() {
        Mention m = new Mention();
        m.setSeuilMinimum(BigDecimal.valueOf(14));
        m.setSeuilMaximum(BigDecimal.valueOf(16));
        assertTrue(m.contient(BigDecimal.valueOf(14)));
        assertTrue(m.contient(BigDecimal.valueOf(16)));
        assertFalse(m.contient(BigDecimal.valueOf(13.99)));
        assertFalse(m.contient(BigDecimal.valueOf(16.01)));
        assertFalse(m.contient(null));
    }
}