package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TypeArrondiTest {

    @Test
    void aucunNeTouchePas() {
        assertEquals(new BigDecimal("10.567"), TypeArrondi.AUCUN.appliquer(new BigDecimal("10.567"), 2));
        assertNull(TypeArrondi.AUCUN.appliquer(null, 2));
    }

    @Test
    void decimaleArronditHalfUp() {
        assertEquals(new BigDecimal("10.57"), TypeArrondi.DECIMALE.appliquer(new BigDecimal("10.567"), 2));
        assertEquals(new BigDecimal("10.57"), TypeArrondi.DECIMALE.appliquer(new BigDecimal("10.565"), 2));
        assertEquals(new BigDecimal("10.56"), TypeArrondi.DECIMALE.appliquer(new BigDecimal("10.564"), 2));
    }

    @Test
    void inferieurEtSuperieur() {
        assertEquals(new BigDecimal("10.56"), TypeArrondi.INFERIEUR.appliquer(new BigDecimal("10.569"), 2));
        assertEquals(new BigDecimal("10.57"), TypeArrondi.SUPERIEUR.appliquer(new BigDecimal("10.561"), 2));
    }
}