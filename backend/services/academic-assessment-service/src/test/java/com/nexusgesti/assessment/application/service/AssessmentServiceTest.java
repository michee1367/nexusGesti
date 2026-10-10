package com.nexusgesti.assessment.application.service;

import com.nexusgesti.assessment.domain.AssessmentException;
import com.nexusgesti.assessment.domain.model.*;
import com.nexusgesti.assessment.domain.port.out.AssessmentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssessmentServiceTest {

    private final AssessmentRepository repository = mock(AssessmentRepository.class);
    private final AssessmentService service = new AssessmentService(repository);

    @Test
    void creerEvaluationPositionneStatutEtReference() {
        when(repository.saveEvaluation(any())).thenAnswer(inv -> inv.getArgument(0));
        Evaluation resultat = service.creerEvaluation(new Evaluation());
        assertEquals(StatutEvaluation.BROUILLON, resultat.getStatut());
        assertNotNull(resultat.getReference());
        assertNotNull(resultat.getCreeLe());
    }

    @Test
    void getEvaluationInconnueLeve404() {
        UUID id = UUID.randomUUID();
        when(repository.findEvaluation(id)).thenReturn(Optional.empty());
        AssessmentException ex = assertThrows(AssessmentException.class, () -> service.getEvaluation(id));
        assertEquals("NOT_FOUND", ex.getCode());
    }

    @Test
    void saisirNotePasseParLaFeuille() {
        UUID id = UUID.randomUUID();
        UUID eleve = UUID.randomUUID();
        Evaluation evaluation = new Evaluation();
        evaluation.setIdEvaluation(id);
        evaluation.setNoteMax(new BigDecimal("20"));
        evaluation.setStatut(StatutEvaluation.BROUILLON);
        evaluation.ouvrir();
        when(repository.findEvaluation(id)).thenReturn(Optional.of(evaluation));
        when(repository.saveEvaluation(any())).thenAnswer(inv -> inv.getArgument(0));

        service.saisirNote(id, eleve, new BigDecimal("13"), UUID.randomUUID());

        assertEquals(1, evaluation.getFeuille().getNbNotesSaisies());
        assertEquals(new BigDecimal("13"), evaluation.getNote(eleve).orElseThrow().getValeur());
    }

    @Test
    void creerBulletinPositionneStatutVersionEtAuteur() {
        when(repository.saveBulletin(any())).thenAnswer(inv -> inv.getArgument(0));
        UUID par = UUID.randomUUID();
        Bulletin resultat = service.creerBulletin(new Bulletin(), par);
        assertEquals(StatutBulletin.BROUILLON, resultat.getStatut());
        assertEquals(1, resultat.getVersion());
        assertEquals(par, resultat.getCreePar());
        assertNotNull(resultat.getReference());
    }

    @Test
    void creerCouponNumeroteEtCalculeLEmpreinte() {
        UUID bulletinId = UUID.randomUUID();
        Bulletin bulletin = new Bulletin();
        bulletin.setIdBulletin(bulletinId);
        bulletin.setEleveId(UUID.randomUUID());
        bulletin.setAnneeId(UUID.randomUUID());
        bulletin.setMoyenneGenerale(new BigDecimal("14.25"));
        when(repository.findBulletin(bulletinId)).thenReturn(Optional.of(bulletin));
        when(repository.countCouponsAnnee(any())).thenReturn(2L);
        when(repository.saveCoupon(any())).thenAnswer(inv -> inv.getArgument(0));

        CouponNotes coupon = service.creerCoupon(bulletinId, "remis au tuteur");

        assertEquals("CN-000003", coupon.getNumero());
        assertTrue(coupon.verifierEmpreinte());
        assertEquals(new BigDecimal("14.25"), coupon.getNoteMoyenne());
    }

    @Test
    void moyenneEleveFiltreParPeriode() {
        UUID eleve = UUID.randomUUID();
        UUID periode = UUID.randomUUID();
        UUID eval1 = UUID.randomUUID();
        UUID eval2 = UUID.randomUUID();
        when(repository.listNotesEleve(eleve)).thenReturn(List.of(
                note(eleve, eval1, "10"), note(eleve, eval2, "20")));
        when(repository.listEvaluationsByIds(any())).thenReturn(List.of(
                evaluation(eval1, periode), evaluation(eval2, UUID.randomUUID())));

        ResultatMoyenne resultat = service.moyenneEleve(eleve, periode);

        assertEquals(new BigDecimal("10.00"), resultat.getMoyenne());
        assertEquals(1, resultat.getNbNotes());
    }

    @Test
    void classementClasseOrdonneLesEleves() {
        UUID classe = UUID.randomUUID();
        UUID eval = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(repository.listEvaluationsClasse(classe)).thenReturn(List.of(evaluation(eval, null)));
        when(repository.listNotesByEvaluationIds(any())).thenReturn(List.of(
                note(a, eval, "11"), note(b, eval, "16")));

        List<Classement> classement = service.classementClasse(classe);

        assertEquals(2, classement.size());
        assertEquals(b, classement.get(0).getEleveId());
        assertEquals(1, classement.get(0).getRang());
    }

    @Test
    void classementClasseSansEvaluationRetourneVide() {
        UUID classe = UUID.randomUUID();
        when(repository.listEvaluationsClasse(classe)).thenReturn(List.of());
        assertTrue(service.classementClasse(classe).isEmpty());
    }

    private Evaluation evaluation(UUID id, UUID periode) {
        Evaluation e = new Evaluation();
        e.setIdEvaluation(id);
        e.setPeriodeId(periode);
        return e;
    }

    private Note note(UUID eleve, UUID evaluationId, String valeur) {
        Note n = new Note();
        n.setEleveId(eleve);
        n.setEvaluationId(evaluationId);
        n.saisir(new BigDecimal(valeur), null, new BigDecimal("20"));
        return n;
    }
}
