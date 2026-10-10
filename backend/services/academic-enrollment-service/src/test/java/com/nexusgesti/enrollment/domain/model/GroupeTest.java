package com.nexusgesti.enrollment.domain.model;

import com.nexusgesti.enrollment.domain.EnrollmentException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupeTest {

    private Groupe groupe(int capacite, StatutGroupe statut) {
        Groupe g = new Groupe();
        g.setIdGroupe(UUID.randomUUID());
        g.setCode("TD1");
        g.setLibelle("TD 1");
        g.setType(TypeGroupe.TD);
        g.setCapacite(capacite);
        g.setStatut(statut);
        return g;
    }

    private MembreGroupe membre() {
        MembreGroupe m = new MembreGroupe();
        m.setEleveId(UUID.randomUUID());
        return m;
    }

    @Test
    void ouvrirDepuisClosRefuse() {
        Groupe g = groupe(10, StatutGroupe.CLOS);
        EnrollmentException ex = assertThrows(EnrollmentException.class, g::ouvrir);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void ajouterMembreMetAJourEffectif() {
        Groupe g = groupe(2, StatutGroupe.OUVERT);
        g.ajouterMembre(membre());
        assertEquals(1, g.getEffectif());
        assertEquals(1, g.getPlacesRestantes());
    }

    @Test
    void groupeDevientComplet() {
        Groupe g = groupe(1, StatutGroupe.OUVERT);
        g.ajouterMembre(membre());
        assertTrue(g.estComplet());
        assertEquals(StatutGroupe.COMPLET, g.getStatut());
    }

    @Test
    void ajouterSurGroupeCompletRefuse() {
        Groupe g = groupe(1, StatutGroupe.OUVERT);
        g.ajouterMembre(membre());
        EnrollmentException ex = assertThrows(EnrollmentException.class, () -> g.ajouterMembre(membre()));
        assertEquals("GROUPE_COMPLET", ex.getCode());
    }

    @Test
    void ajouterSurGroupeClosRefuse() {
        Groupe g = groupe(10, StatutGroupe.CLOS);
        EnrollmentException ex = assertThrows(EnrollmentException.class, () -> g.ajouterMembre(membre()));
        assertEquals("ETAT_INVALIDE", ex.getCode());
    }

    @Test
    void exclusionReouvreLeGroupe() {
        Groupe g = groupe(1, StatutGroupe.OUVERT);
        MembreGroupe m = membre();
        g.ajouterMembre(m);
        assertTrue(g.exclureMembre(m.getIdMembre()).isPresent());
        assertFalse(g.estComplet());
        assertEquals(StatutGroupe.OUVERT, g.getStatut());
        assertEquals(0, g.getEffectif());
    }

    @Test
    void capaciteNulleNestJamaisComplete() {
        Groupe g = groupe(0, StatutGroupe.OUVERT);
        g.ajouterMembre(membre());
        assertFalse(g.estComplet());
    }
}
