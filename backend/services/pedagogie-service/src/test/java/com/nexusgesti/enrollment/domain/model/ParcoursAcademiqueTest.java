package com.nexusgesti.enrollment.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParcoursAcademiqueTest {

    private InscriptionUE validee(UUID ueId, BigDecimal credits, BigDecimal moyenne) {
        InscriptionUE i = new InscriptionUE();
        i.setUeId(ueId);
        i.setStatut(StatutInscriptionUE.VALIDE);
        i.setCreditsReconnus(credits);
        i.setMoyenneUEPrecedente(moyenne);
        return i;
    }

    private InscriptionUE nonValidee(UUID ueId, BigDecimal credits) {
        InscriptionUE i = new InscriptionUE();
        i.setUeId(ueId);
        i.setStatut(StatutInscriptionUE.INSCRIT);
        i.setCreditsReconnus(credits);
        return i;
    }

    @Test
    void ueAcquiseSeulementSiValidee() {
        UUID ue = UUID.randomUUID();
        ParcoursAcademique p = new ParcoursAcademique();
        p.setInscriptions(List.of(validee(ue, new BigDecimal("6"), null), nonValidee(UUID.randomUUID(), new BigDecimal("6"))));
        assertTrue(p.estUeAcquise(ue));
        assertTrue(!p.estUeAcquise(UUID.randomUUID()));
    }

    @Test
    void creditsAcquisCumulentLesUeValidees() {
        ParcoursAcademique p = new ParcoursAcademique();
        p.setInscriptions(List.of(
                validee(UUID.randomUUID(), new BigDecimal("6"), null),
                validee(UUID.randomUUID(), new BigDecimal("4"), null),
                nonValidee(UUID.randomUUID(), new BigDecimal("10"))));
        assertEquals(new BigDecimal("10"), p.getCreditsAcquis());
    }

    @Test
    void tauxProgression() {
        ParcoursAcademique p = new ParcoursAcademique();
        p.setCreditsRequis(new BigDecimal("60"));
        p.setInscriptions(List.of(validee(UUID.randomUUID(), new BigDecimal("30"), null)));
        assertEquals(new BigDecimal("0.5000"), p.getTauxProgression());
    }

    @Test
    void tauxProgressionSansCreditsRequis() {
        ParcoursAcademique p = new ParcoursAcademique();
        assertEquals(BigDecimal.ZERO, p.getTauxProgression());
    }

    @Test
    void moyennePonderee() {
        ParcoursAcademique p = new ParcoursAcademique();
        p.setInscriptions(List.of(
                validee(UUID.randomUUID(), new BigDecimal("6"), new BigDecimal("12")),
                validee(UUID.randomUUID(), new BigDecimal("3"), new BigDecimal("10"))));
        assertEquals(new BigDecimal("11.33"), p.getMoyennePonderee());
    }

    @Test
    void moyennePondereeNulleSansNote() {
        ParcoursAcademique p = new ParcoursAcademique();
        p.setInscriptions(List.of(validee(UUID.randomUUID(), new BigDecimal("6"), null)));
        assertNull(p.getMoyennePonderee());
    }
}
