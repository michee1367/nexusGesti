package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BesoinApprovisionnementTest {

    @Test
    void suggestionEgaleSeuilPlusConsommationMoinsDisponible() {
        BesoinApprovisionnement besoin = BesoinApprovisionnement.calculer(
                "FARINE", "Farine", new BigDecimal("20"), new BigDecimal("50"),
                new BigDecimal("30"), 3);

        assertEquals(0, besoin.quantiteSuggeree().compareTo(new BigDecimal("60.0000")));
        assertEquals("HAUTE", besoin.urgence());
        assertTrue(besoin.estSousSeuil());
    }

    @Test
    void suggestionJamaisNegative() {
        BesoinApprovisionnement besoin = BesoinApprovisionnement.calculer(
                "SEL", "Sel", new BigDecimal("100"), new BigDecimal("10"),
                new BigDecimal("5"), 2);

        assertEquals(0, besoin.quantiteSuggeree().compareTo(BigDecimal.ZERO));
        assertEquals("NORMALE", besoin.urgence());
        assertFalse(besoin.estSousSeuil());
    }

    @Test
    void ruptureQuandPlusDeStock() {
        BesoinApprovisionnement besoin = BesoinApprovisionnement.calculer(
                "LEVURE", "Levure", BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, 1);

        assertEquals("RUPTURE", besoin.urgence());
        assertTrue(besoin.estSousSeuil());
        assertEquals(0, besoin.quantiteSuggeree().compareTo(new BigDecimal("10.0000")));
    }

    @Test
    void hauteurQuandDisponibiliteSousLeSeuil() {
        BesoinApprovisionnement besoin = BesoinApprovisionnement.calculer(
                "BEURRE", "Beurre", new BigDecimal("4.9999"), new BigDecimal("5"),
                BigDecimal.ZERO, null);

        assertEquals("HAUTE", besoin.urgence());
        assertNull(besoin.delaiReapproJours());
    }

    @Test
    void nulsTraitesCommeZero() {
        BesoinApprovisionnement besoin = BesoinApprovisionnement.calculer(
                "EAU", "Eau", null, null, null, null);

        assertEquals(0, besoin.quantiteDisponible().compareTo(BigDecimal.ZERO));
        assertEquals(0, besoin.quantiteSuggeree().compareTo(BigDecimal.ZERO));
        assertEquals("RUPTURE", besoin.urgence());
    }
}
