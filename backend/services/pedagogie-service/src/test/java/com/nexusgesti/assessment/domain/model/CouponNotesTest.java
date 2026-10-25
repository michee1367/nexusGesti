package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CouponNotesTest {

    private CouponNotes coupon() {
        CouponNotes c = new CouponNotes();
        c.setId(UUID.randomUUID());
        c.setNumero("CN-000001");
        c.setBulletinId(UUID.randomUUID());
        c.setEleveId(UUID.randomUUID());
        c.setNoteMoyenne(new BigDecimal("14.50"));
        c.setDateEdition(LocalDate.now());
        return c;
    }

    @Test
    void cycleDeVieComplet() {
        CouponNotes c = coupon();
        assertEquals(StatutCoupon.A_TIRER, c.getStatut());
        c.tirer().detacher(null).signer(null).rendre();
        assertEquals(StatutCoupon.RENDU, c.getStatut());
        assertTrue(c.estSigne());
        assertTrue(c.estRendu());
    }

    @Test
    void tirerDeuxFoisRefuse() {
        CouponNotes c = coupon().tirer();
        assertThrows(AssessmentException.class, c::tirer);
    }

    @Test
    void signatureSansDetachementRefusee() {
        CouponNotes c = coupon().tirer();
        assertThrows(AssessmentException.class, () -> c.signer(null));
    }

    @Test
    void empreinteVerifieeEtDetecteModification() {
        CouponNotes c = coupon();
        c.calculerEmpreinte();
        assertTrue(c.verifierEmpreinte());
        c.setNoteMoyenne(new BigDecimal("19.99"));
        assertFalse(c.verifierEmpreinte());
    }

    @Test
    void texteResumeLeCoupon() {
        CouponNotes c = coupon();
        assertTrue(c.getTexte().contains("CN-000001"));
        assertTrue(c.getTexte().contains("14.50"));
    }
}
