package com.nexusgesti.people.domain.model;

import com.nexusgesti.people.domain.PeopleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscriptionTest {

    @Test
    void validationDepuisEnAttente() {
        Inscription i = new Inscription();
        i.setStatut(StatutInscription.EN_ATTENTE);
        i.valider();
        assertEquals(StatutInscription.VALIDE, i.getStatut());
        assertTrue(i.estEffectivementInscrit());
    }

    @Test
    void validationDepuisValideRefusee() {
        Inscription i = new Inscription();
        i.setStatut(StatutInscription.VALIDE);
        PeopleException ex = assertThrows(PeopleException.class, i::valider);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void annulationConserveLeMotif() {
        Inscription i = new Inscription();
        i.setStatut(StatutInscription.EN_ATTENTE);
        i.annuler("Desistement");
        assertEquals(StatutInscription.ANNULEE, i.getStatut());
        assertEquals("Desistement", i.getMotifAnnulation());
        assertFalse(i.estEffectivementInscrit());
    }

    @Test
    void annulationDuneInscriptionDejaAnnuleeRefusee() {
        Inscription i = new Inscription();
        i.setStatut(StatutInscription.ANNULEE);
        assertThrows(PeopleException.class, () -> i.annuler("x"));
    }
}
