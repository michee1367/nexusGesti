package com.nexusgesti.people.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EleveTest {

    private Eleve eleve(StatutEleve statut, LocalDate naissance, LocalDate entree) {
        Eleve e = new Eleve();
        e.setIdEleve(UUID.randomUUID());
        e.setMatricule("ELV-001");
        e.setStatut(statut);
        e.setDateNaissance(naissance);
        e.setDateInscription(entree);
        e.setStatutDossier(StatutDossier.EN_COURS);
        return e;
    }

    @Test
    void estInscritDansLaPeriode() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        assertTrue(e.estInscrit(LocalDate.of(2021, 1, 1)));
        assertFalse(e.estInscrit(LocalDate.of(2019, 1, 1)));
    }

    @Test
    void nonInscritApresSortie() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        e.setDateSortie(LocalDate.of(2021, 6, 30));
        assertFalse(e.estInscrit(LocalDate.of(2021, 7, 1)));
    }

    @Test
    void getAge() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        assertEquals(10, e.getAge(LocalDate.of(2020, 1, 1)));
    }

    @Test
    void transferer() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        e.transferer("Ecole B", "Demenagement");
        assertEquals(StatutEleve.TRANSFERE, e.getStatut());
        assertEquals("Ecole B", e.getEtablissementDestination());
        assertNotNull(e.getDateSortie());
    }

    @Test
    void radier() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        e.radier("Abandon");
        assertEquals(StatutEleve.RADIE, e.getStatut());
    }

    @Test
    void obtenirDossierCreeUnDossierAuBesoin() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        DossierEleve d = e.obtenirDossier();
        assertNotNull(d.getId());
        assertEquals(e.getIdEleve(), d.getEleveId());
        assertEquals("DOS-ELV-001", d.getReference());
        assertEquals(StatutDossier.EN_COURS, d.getStatut());
        assertEquals(d, e.obtenirDossier());
    }

    @Test
    void classeCouranteRetientLaDerniereInscriptionValide() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        UUID ancienne = UUID.randomUUID();
        UUID nouvelle = UUID.randomUUID();
        e.ajouterInscription(inscription(ancienne, StatutInscription.VALIDE, LocalDate.of(2020, 9, 1)));
        e.ajouterInscription(inscription(nouvelle, StatutInscription.VALIDE, LocalDate.of(2021, 9, 1)));
        e.ajouterInscription(inscription(UUID.randomUUID(), StatutInscription.EN_ATTENTE, LocalDate.of(2022, 9, 1)));
        assertEquals(nouvelle, e.getClasseCourante(LocalDate.of(2022, 1, 1)));
    }

    @Test
    void classeCouranteNullSansInscriptionValide() {
        Eleve e = eleve(StatutEleve.INSCRIT, LocalDate.of(2010, 1, 1), LocalDate.of(2020, 9, 1));
        assertNull(e.getClasseCourante(LocalDate.of(2021, 1, 1)));
    }

    private Inscription inscription(UUID classeId, StatutInscription statut, LocalDate date) {
        Inscription i = new Inscription();
        i.setClasseId(classeId);
        i.setStatut(statut);
        i.setDateInscription(date);
        return i;
    }
}
