package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionCaisseTest {

    @Test
    void ouvrirEtEncaisserPuisCloturer() {
        SessionCaisse session = new SessionCaisse(LocalDate.now(), new BigDecimal("100.00"), UUID.randomUUID());
        session.encaisser(new BigDecimal("25.50"));
        session.encaisser(new BigDecimal("10.00"));
        assertEquals(new BigDecimal("35.50"), session.getTotalEncaisse());
        session.cloturer(new BigDecimal("135.50"));
        assertTrue(session.isCloturee());
        assertEquals(new BigDecimal("0.00"), session.getEcart());
    }

    @Test
    void cloturerAvecEcart() {
        SessionCaisse session = new SessionCaisse(LocalDate.now(), BigDecimal.ZERO, UUID.randomUUID());
        session.encaisser(new BigDecimal("20.00"));
        session.cloturer(new BigDecimal("18.00"));
        assertEquals(new BigDecimal("-2.00"), session.getEcart());
    }

    @Test
    void impossibleEncaisserApresCloture() {
        SessionCaisse session = new SessionCaisse(LocalDate.now(), BigDecimal.ZERO, UUID.randomUUID());
        session.cloturer(BigDecimal.ZERO);
        assertThrows(VenteException.class, () -> session.encaisser(BigDecimal.TEN));
    }
}
