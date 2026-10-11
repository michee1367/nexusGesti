package com.nexusgesti.exam.application.service;

import com.nexusgesti.exam.domain.ExamException;
import com.nexusgesti.exam.domain.model.*;
import com.nexusgesti.exam.domain.port.out.ExamRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExamServiceTest {

    private final ExamRepository repository = mock(ExamRepository.class);
    private final ExamService service = new ExamService(repository);

    @Test
    void creerSessionPositionneReferenceEtStatut() {
        when(repository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        SessionExamen resultat = service.creerSession(new SessionExamen());
        assertEquals(StatutSession.BROUILLON, resultat.getStatut());
        assertNotNull(resultat.getReference());
        assertNotNull(resultat.getDateOuverture());
    }

    @Test
    void getSessionInconnueLeve404() {
        UUID id = UUID.randomUUID();
        when(repository.findSession(id)).thenReturn(Optional.empty());
        ExamException ex = assertThrows(ExamException.class, () -> service.getSession(id));
        assertEquals("NOT_FOUND", ex.getCode());
    }

    @Test
    void programmationPuisOuverturePuisCloture() {
        UUID id = UUID.randomUUID();
        SessionExamen session = new SessionExamen();
        session.setIdSession(id);
        when(repository.findSession(id)).thenReturn(Optional.of(session));
        when(repository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));

        service.programmerSession(id);
        service.ouvrirSession(id);
        SessionExamen cloturee = service.cloturerSession(id);

        assertEquals(StatutSession.CLOTUREE, cloturee.getStatut());
    }

    @Test
    void ajouterEpreuvePositionneReferenceEtStatut() {
        UUID sessionId = UUID.randomUUID();
        SessionExamen session = new SessionExamen();
        session.setIdSession(sessionId);
        when(repository.findSession(sessionId)).thenReturn(Optional.of(session));
        when(repository.saveEpreuve(any())).thenAnswer(inv -> inv.getArgument(0));

        Epreuve epreuve = service.ajouterEpreuve(sessionId, new Epreuve());

        assertEquals(sessionId, epreuve.getSessionId());
        assertEquals(StatutEpreuve.PROJETEE, epreuve.getStatut());
        assertNotNull(epreuve.getReference());
    }

    @Test
    void saisirNoteRecupereLaNoteExistante() {
        UUID epreuveId = UUID.randomUUID();
        UUID eleveId = UUID.randomUUID();
        Epreuve epreuve = new Epreuve();
        epreuve.setIdEpreuve(epreuveId);
        epreuve.setCoefficient(new BigDecimal("2"));
        epreuve.rattacherPeriode(UUID.randomUUID());
        when(repository.findEpreuve(epreuveId)).thenReturn(Optional.of(epreuve));
        when(repository.listNotesEpreuve(epreuveId)).thenReturn(List.of());
        when(repository.saveNote(any())).thenAnswer(inv -> inv.getArgument(0));

        NoteExamen note = service.saisirNoteExamen(epreuveId, eleveId, new BigDecimal("14"), UUID.randomUUID());

        assertEquals(new BigDecimal("14"), note.getValeur());
        assertEquals(new BigDecimal("2"), note.getCoefficient());
        assertFalse(note.isHorsMoyenne());
    }

    @Test
    void creerConvocationGenereNumeroEtUrl() {
        UUID sessionId = UUID.randomUUID();
        SessionExamen session = new SessionExamen();
        session.setIdSession(sessionId);
        when(repository.findSession(sessionId)).thenReturn(Optional.of(session));
        when(repository.countConvocationsSession(sessionId)).thenReturn(2L);
        when(repository.saveConvocation(any())).thenAnswer(inv -> {
            Convocation c = inv.getArgument(0);
            if (c.getId() == null) {
                c.setId(UUID.randomUUID());
            }
            return c;
        });

        Convocation convocation = service.creerConvocation(sessionId, new Convocation());

        assertEquals("CNV-003", convocation.getNumero());
        assertEquals("CNV-003", convocation.getQrCode());
        assertNotNull(convocation.getUrlTelechargement());
        assertTrue(session.isConvocationEmise());
    }

    @Test
    void ouvrirCorrectionCompteLesNotes() {
        UUID epreuveId = UUID.randomUUID();
        Epreuve epreuve = new Epreuve();
        epreuve.setIdEpreuve(epreuveId);
        when(repository.findEpreuve(epreuveId)).thenReturn(Optional.of(epreuve));
        when(repository.listNotesEpreuve(epreuveId)).thenReturn(List.of(new NoteExamen(), new NoteExamen()));
        when(repository.saveCorrection(any())).thenAnswer(inv -> inv.getArgument(0));

        Correction correction = service.ouvrirCorrection(epreuveId, UUID.randomUUID());

        assertEquals(StatutCorrection.OUVERTE, correction.getStatut());
        assertEquals(2, correction.getNbNotesCorrigees());
    }

    @Test
    void deciderExigeDeliberationEnCours() {
        UUID id = UUID.randomUUID();
        Deliberation deliberation = new Deliberation();
        deliberation.setId(id);
        when(repository.findDeliberation(id)).thenReturn(Optional.of(deliberation));
        assertThrows(ExamException.class, () -> service.decider(id, UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void deciderAjouteUneSeuleDecision() {
        UUID id = UUID.randomUUID();
        Deliberation deliberation = new Deliberation();
        deliberation.setId(id);
        deliberation.ouvrir();
        when(repository.findDeliberation(id)).thenReturn(Optional.of(deliberation));
        when(repository.saveDeliberation(any())).thenAnswer(inv -> inv.getArgument(0));

        Decision decision = service.decider(id, UUID.randomUUID(), UUID.randomUUID());

        assertNotNull(decision.getId());
        assertEquals(1, deliberation.getDecisions().size());
    }

    @Test
    void publierResultatMarqueEtPositionneUrlApresSauvegarde() {
        UUID decisionId = UUID.randomUUID();
        when(repository.saveResultat(any())).thenAnswer(inv -> {
            ResultatOfficiel r = inv.getArgument(0);
            if (r.getId() == null) {
                r.setId(UUID.randomUUID());
            }
            return r;
        });

        ResultatOfficiel resultat = service.publierResultat(decisionId, UUID.randomUUID());

        assertTrue(resultat.isPublie());
        assertNotNull(resultat.getDatePublication());
        assertNotNull(resultat.getUrlDocument());
        assertTrue(resultat.getUrlDocument().startsWith("/api/v1/ped/resultats/"));
    }

    @Test
    void creerProcesVerbalRedigeEtPositionneReference() {
        when(repository.saveProcesVerbal(any())).thenAnswer(inv -> inv.getArgument(0));
        UUID par = UUID.randomUUID();
        ProcesVerbal pv = service.creerProcesVerbal(new ProcesVerbal(), par);
        assertNotNull(pv.getReference());
        assertEquals(par, pv.getRedigePar());
        assertEquals(StatutPV.BROUILLON, pv.getStatut());
    }
}
