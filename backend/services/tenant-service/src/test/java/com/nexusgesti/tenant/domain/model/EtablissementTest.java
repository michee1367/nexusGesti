package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EtablissementTest {

    private Etablissement etablissement() {
        Etablissement e = new Etablissement();
        e.setId(UUID.randomUUID());
        e.setCode("NEX");
        e.setDenomination("NexusGesti");
        e.setProfil(ProfilEtablissement.SECONDAIRE);
        e.setStatut(StatutEtablissement.PREPARATION);
        return e;
    }

    @Test
    void preparationPeutPasserActif() {
        Etablissement e = etablissement().activer();
        assertEquals(StatutEtablissement.ACTIF, e.getStatut());
        assertTrue(e.estOperatif());
    }

    @Test
    void suspensionRequiertUnMotif() {
        Etablissement e = etablissement().activer();
        assertThrows(TenantException.class, () -> e.suspendre("  "));
        assertEquals(StatutEtablissement.ACTIF, e.getStatut());
    }

    @Test
    void suspensionDepuisPreparationEstInterdite() {
        Etablissement e = etablissement();
        TenantException ex = assertThrows(TenantException.class, () -> e.suspendre("fermeture"));
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void actifSuspenduPeutEtreArchive() {
        Etablissement e = etablissement().activer().suspendre("inspection");
        e.archiver();
        assertEquals(StatutEtablissement.ARCHIVE, e.getStatut());
    }

    @Test
    void archiveNePeutPasRepartir() {
        Etablissement e = etablissement().activer().archiver();
        assertThrows(TenantException.class, e::activer);
        assertThrows(TenantException.class, () -> e.suspendre("motif"));
    }

    @Test
    void newArriveEnPreparationParDefaut() {
        assertNotNull(etablissement());
    }
}