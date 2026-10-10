package com.nexusgesti.enrollment.domain.model;

import com.nexusgesti.enrollment.domain.EnrollmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscriptionUETest {

    private InscriptionUE inscription(StatutInscriptionUE statut) {
        InscriptionUE i = new InscriptionUE();
        i.setEleveId(UUID.randomUUID());
        i.setUeId(UUID.randomUUID());
        i.setStatut(statut);
        return i;
    }

    @Test
    void inscriptionDepuisVide() {
        InscriptionUE i = inscription(null).inscrire();
        assertEquals(StatutInscriptionUE.INSCRIT, i.getStatut());
        assertNotNull(i.getDateInscription());
    }

    @Test
    void inscriptionDepuisValideRefusee() {
        InscriptionUE i = inscription(StatutInscriptionUE.VALIDE);
        EnrollmentException ex = assertThrows(EnrollmentException.class, i::inscrire);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void validationDepuisInscrit() {
        UUID par = UUID.randomUUID();
        InscriptionUE i = inscription(StatutInscriptionUE.INSCRIT).valider(par);
        assertEquals(StatutInscriptionUE.VALIDE, i.getStatut());
        assertNotNull(i.getValideLe());
        assertEquals(par, i.getValidePar());
    }

    @Test
    void validationDepuisAnnuleRefusee() {
        InscriptionUE i = inscription(StatutInscriptionUE.ANNULE);
        assertThrows(EnrollmentException.class, () -> i.valider(UUID.randomUUID()));
    }

    @Test
    void refusDepuisValideRefuse() {
        InscriptionUE i = inscription(StatutInscriptionUE.VALIDE);
        assertThrows(EnrollmentException.class, () -> i.refuser("trop tard"));
    }

    @Test
    void refusEnregistreLeMotif() {
        InscriptionUE i = inscription(StatutInscriptionUE.INSCRIT).refuser("moyenne insuffisante");
        assertEquals(StatutInscriptionUE.REFUSE, i.getStatut());
        assertEquals("moyenne insuffisante", i.getMotifRefus());
    }

    @Test
    void listeAttenteEnregistreLeRang() {
        InscriptionUE i = inscription(StatutInscriptionUE.INSCRIT).placerEnListeAttente(3);
        assertEquals(StatutInscriptionUE.EN_LISTE_ATTENTE, i.getStatut());
        assertEquals(3, i.getRangListeAttente());
    }

    @Test
    void annulationDepuisValideRefusee() {
        InscriptionUE i = inscription(StatutInscriptionUE.VALIDE);
        assertThrows(EnrollmentException.class, i::annuler);
    }

    @Test
    void creditsAcquisSeulementSiValide() {
        InscriptionUE i = inscription(StatutInscriptionUE.INSCRIT);
        i.setCreditsReconnus(new BigDecimal("6"));
        assertEquals(BigDecimal.ZERO, i.getCreditsAcquis());
        assertEquals(new BigDecimal("6"), i.getCreditsRestants());

        i.valider(UUID.randomUUID());
        assertEquals(new BigDecimal("6"), i.getCreditsAcquis());
        assertEquals(BigDecimal.ZERO, i.getCreditsRestants());
    }

    @Test
    void estRattape() {
        assertTrue(inscription(StatutInscriptionUE.RATTAPE).estRattape());
        assertFalse(inscription(StatutInscriptionUE.VALIDE).estRattape());
    }
}
