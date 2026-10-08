package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrillesOperationnellesTest {

    @Test
    void ecartInventaireDansLaTolerance() {
        GrilleStock s = new GrilleStock();
        s.setToleranceEcartInventairePct(new BigDecimal("2.00"));
        assertTrue(s.estEcartInventaireTolere(new BigDecimal("2.00")));
        assertFalse(s.estEcartInventaireTolere(new BigDecimal("2.01")));
        assertFalse(s.estEcartInventaireTolere(null));
    }

    @Test
    void seuilsStockAvecDefaut() {
        GrilleStock s = new GrilleStock();
        assertEquals(0, s.getDureeQuarantaine());
        assertEquals(0, s.getDelaiReappro());
        s.setDureeQuarantaineJours(30);
        s.setDelaiReapproJours(15);
        assertEquals(30, s.getDureeQuarantaine());
        assertEquals(15, s.getDelaiReappro());
    }

    @Test
    void retentionParObjet() {
        GrilleConformite c = new GrilleConformite();
        c.setDureeRetentionDossierMedical(10);
        c.setDureeConservationResultats(5);
        assertEquals(10, c.getDureeRetention("DOSSIER_MEDICAL"));
        assertEquals(5, c.getDureeRetention("RESULTATS"));
        assertEquals(0, c.getDureeRetention("INCONNU"));
    }

    @Test
    void archivageSousConditions() {
        GrilleConformite c = new GrilleConformite();
        c.setArchivable(true);
        c.setDureeRetentionDossierMedical(1);
        Date ancien = new Date(System.currentTimeMillis() - 2L * 86400_000L);
        Date recent = new Date();
        assertTrue(c.estArchivable("DOSSIER_MEDICAL", ancien));
        assertFalse(c.estArchivable("DOSSIER_MEDICAL", recent));
        c.setArchivable(false);
        assertFalse(c.estArchivable("DOSSIER_MEDICAL", ancien));
    }

    @Test
    void accesTraceEtDelaiCritique() {
        GrilleConformite c = new GrilleConformite();
        c.setAccesTracable(true);
        c.setDelaiDeclarationResultatCritique(4);
        assertTrue(c.exigeAccesTrace());
        assertEquals(4, c.getDelaiDeclarationCritique());
    }
}