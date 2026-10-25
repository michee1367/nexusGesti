package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionExamenTest {

    @Test
    void cycleDeVieComplet() {
        SessionExamen session = new SessionExamen();
        session.setIdSession(UUID.randomUUID());
        assertEquals(StatutSession.BROUILLON, session.getStatut());

        session.programmer();
        assertEquals(StatutSession.PROGRAMMEE, session.getStatut());

        session.ouvrir();
        assertEquals(StatutSession.EN_COURS, session.getStatut());
        assertNotNull(session.getDateOuverture());

        session.cloturer();
        assertEquals(StatutSession.CLOTUREE, session.getStatut());
        assertNotNull(session.getDateCloture());
    }

    @Test
    void ouvrirUneSessionBrouillonEstInterdit() {
        SessionExamen session = new SessionExamen();
        assertThrows(ExamException.class, session::ouvrir);
    }

    @Test
    void annulerExigeUnMotif() {
        SessionExamen session = new SessionExamen();
        assertThrows(ExamException.class, () -> session.annuler(" "));
        session.annuler("session reportee");
        assertEquals(StatutSession.ANNULEE, session.getStatut());
        assertNotNull(session.getAnnuleLe());
    }

    @Test
    void estAnnuelleSansPeriodeUniversitaire() {
        SessionExamen session = new SessionExamen();
        assertFalse(session.estAnnuelle());
        session.setAnneeUniversitaire(true);
        assertTrue(session.estAnnuelle());
        session.setPeriodeId(UUID.randomUUID());
        assertFalse(session.estAnnuelle());
    }

    @Test
    void ajouterEpreuvePositionneSessionEtId() {
        SessionExamen session = new SessionExamen();
        session.setIdSession(UUID.randomUUID());
        Epreuve epreuve = new Epreuve();
        session.ajouterEpreuve(epreuve);
        assertEquals(session.getIdSession(), epreuve.getSessionId());
        assertNotNull(epreuve.getIdEpreuve());
        assertEquals(1, session.getEpreuves().size());
    }

    @Test
    void settersCollectionCopientDefensivement() {
        SessionExamen session = new SessionExamen();
        var source = new java.util.ArrayList<Epreuve>();
        session.setEpreuves(source);
        source.add(new Epreuve());
        assertTrue(session.getEpreuves().isEmpty());
    }
}
