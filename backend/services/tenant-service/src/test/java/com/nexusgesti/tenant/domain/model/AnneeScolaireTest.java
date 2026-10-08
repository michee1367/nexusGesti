package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnneeScolaireTest {

    private AnneeScolaire annee(StatutAnnee statut, boolean avecPeriodeCloturee) {
        AnneeScolaire a = new AnneeScolaire();
        a.setIdAnnee(UUID.randomUUID());
        a.setEtablissementId(UUID.randomUUID());
        a.setCode("2026-2027");
        a.setLibelle("Annee scolaire");
        a.setType(TypeAnnee.SCOLAIRE);
        a.setStatut(statut);
        a.setAnneeReference(2026);
        if (avecPeriodeCloturee) {
            Periode p = new Periode();
            p.setCloturee(true);
            a.getPeriodes().add(p);
        }
        return a;
    }

    @Test
    void preparationOuverture() {
        AnneeScolaire a = annee(StatutAnnee.PREPARATION, false).ouvrir();
        assertEquals(StatutAnnee.EN_COURS, a.getStatut());
        assertTrue(a.estCourante());
    }

    @Test
    void clotureePeutEtreReouverte() {
        AnneeScolaire a = annee(StatutAnnee.CLOTUREE, false).ouvrir();
        assertEquals(StatutAnnee.EN_COURS, a.getStatut());
    }

    @Test
    void cloturerExigePeriodeCloturee() {
        AnneeScolaire a = annee(StatutAnnee.EN_COURS, false);
        TenantException ex = assertThrows(TenantException.class, () -> a.cloturer(new Date()));
        assertEquals("PERIODES_OUVERTES", ex.getCode());
    }

    @Test
    void cloturerDepuisPreparationEstInterdit() {
        AnneeScolaire a = annee(StatutAnnee.PREPARATION, true);
        assertThrows(TenantException.class, () -> a.cloturer(new Date()));
    }

    @Test
    void cloturerAvecPeriodesCloturees() {
        AnneeScolaire a = annee(StatutAnnee.EN_COURS, true).cloturer(new Date());
        assertEquals(StatutAnnee.CLOTUREE, a.getStatut());
    }

    @Test
    void contientRespecteLeCadre() {
        AnneeScolaire a = new AnneeScolaire();
        a.setDateDebut(new Date(0));
        a.setDateFin(new Date(86400000L));
        assertTrue(a.contient(new Date(43200000L)));
        assertFalse(a.contient(new Date(86400001L)));
        assertFalse(a.contient(null));
    }
}