package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferentielTest {

    private Referentiel referentiel() {
        Referentiel r = new Referentiel();
        r.setIdReferentiel(UUID.randomUUID());
        r.setEtablissementId(UUID.randomUUID());
        r.setCode("REF");
        r.setNature(NatureReferentiel.MOTIF_ABSENCE);
        r.setOrdre(1);
        return r;
    }

    @Test
    void ajouterItemAffecteUnOrdreAutomatique() {
        Referentiel r = referentiel();
        r.ajouterItem("Maladie", "MAL");
        r.ajouterItem("Convoi", "CONV");
        assertEquals(2, r.getItems().size());
        assertEquals(0, r.getItems().get(0).getOrdre());
        assertEquals(1, r.getItems().get(1).getOrdre());
        assertTrue(r.getItems().get(0).isActif());
    }

    @Test
    void codeEnDoubleRefuse() {
        Referentiel r = referentiel();
        r.ajouterItem("Maladie", "MAL");
        TenantException ex = assertThrows(TenantException.class, () -> r.ajouterItem("Autre", "MAL"));
        assertEquals("ITEM_EXISTANT", ex.getCode());
    }

    @Test
    void desactiverMetInactifSansSupprimer() {
        Referentiel r = referentiel();
        r.ajouterItem("Maladie", "MAL");
        r.desactiver("MAL");
        assertFalse(r.getItems().get(0).isActif());
    }

    @Test
    void itemBloqueSaisieNonCochable() {
        ReferentielItem item = new ReferentielItem();
        item.setBloqueSaisie(true);
        assertFalse(item.estCocheable());
        item.setBloqueSaisie(null);
        assertTrue(item.estCocheable());
    }
}