package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EpreuveEtConvocationTest {

    @Test
    void validerEpreuve() {
        Epreuve epreuve = new Epreuve();
        assertEquals(StatutEpreuve.PROJETEE, epreuve.getStatut());
        epreuve.valider();
        assertEquals(StatutEpreuve.VALIDEE, epreuve.getStatut());
    }

    @Test
    void estHorsMoyenneSiNonComptabiliseeOuSansPeriode() {
        Epreuve epreuve = new Epreuve();
        assertTrue(epreuve.estHorsMoyenne());
        epreuve.rattacherPeriode(UUID.randomUUID());
        assertFalse(epreuve.estHorsMoyenne());
        epreuve.declarerHorsMoyenne();
        assertTrue(epreuve.estHorsMoyenne());
    }

    @Test
    void getPointsMaxDepuisBareme() {
        Epreuve epreuve = new Epreuve();
        epreuve.setBareme("20");
        assertEquals(new java.math.BigDecimal("20"), epreuve.getPointsMax());
        epreuve.setBareme("abc");
        assertNull(epreuve.getPointsMax());
    }

    @Test
    void convocationEmissionEtNotification() {
        Convocation convocation = new Convocation();
        convocation.setId(UUID.randomUUID());
        convocation.emettre();
        assertEquals(StatutConvocation.EMISE, convocation.getStatut());
        assertNotNull(convocation.getGenereeLe());

        convocation.notifier("EMAIL");
        assertEquals(StatutConvocation.NOTIFIEE, convocation.getStatut());
        assertEquals("EMAIL", convocation.getCanal());
        assertNotNull(convocation.getNotifieeLe());
    }

    @Test
    void ajouterLignePositionneConvocation() {
        Convocation convocation = new Convocation();
        convocation.setId(UUID.randomUUID());
        LigneConvocation ligne = new LigneConvocation();
        convocation.ajouterLigne(ligne);
        assertEquals(convocation.getId(), ligne.getConvocationId());
        assertNotNull(ligne.getId());
    }

    @Test
    void notifierConvocationAnnuleeEstRefuse() {
        Convocation convocation = new Convocation();
        convocation.setId(UUID.randomUUID());
        convocation.setStatut(StatutConvocation.ANNULEE);
        assertThrows(ExamException.class, () -> convocation.notifier("EMAIL"));
    }
}
