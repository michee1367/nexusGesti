package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliberationTest {

    @Test
    void cycleDeVie() {
        Deliberation deliberation = new Deliberation();
        deliberation.setId(UUID.randomUUID());
        assertEquals(StatutDeliberation.PLANIFIEE, deliberation.getStatut());
        deliberation.ouvrir();
        assertEquals(StatutDeliberation.EN_COURS, deliberation.getStatut());
        assertTrue(deliberation.isQuorumAtteint());
        deliberation.cloturer();
        assertEquals(StatutDeliberation.CLOTUREE, deliberation.getStatut());
        assertNotNull(deliberation.getDateCloture());
    }

    @Test
    void deciderHorsSessionEnCoursEstRefuse() {
        Deliberation deliberation = new Deliberation();
        deliberation.setId(UUID.randomUUID());
        assertThrows(ExamException.class,
                () -> deliberation.decider(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void deciderAjouteUneDecision() {
        Deliberation deliberation = new Deliberation();
        deliberation.setId(UUID.randomUUID());
        deliberation.ouvrir();
        Decision decision = deliberation.decider(UUID.randomUUID(), UUID.randomUUID());
        assertNotNull(decision.getId());
        assertEquals(deliberation.getId(), decision.getDeliberationId());
        assertEquals(1, deliberation.getDecisions().size());
    }

    @Test
    void tauxReussiteCalculeSurDecisionsFavorables() {
        Deliberation deliberation = new Deliberation();
        deliberation.setId(UUID.randomUUID());
        deliberation.ouvrir();
        Decision a = deliberation.decider(null, UUID.randomUUID());
        a.setDecision(DecisionType.ADMIS);
        Decision b = deliberation.decider(null, UUID.randomUUID());
        b.setDecision(DecisionType.REDOUBLANT);
        Decision c = deliberation.decider(null, UUID.randomUUID());
        c.setDecision(DecisionType.ADMIS_MENTION);

        assertEquals(new BigDecimal("66.67"), deliberation.getTauxReussite());
    }

    @Test
    void ajouterDecisionUEPositionneDeliberation() {
        Deliberation deliberation = new Deliberation();
        deliberation.setId(UUID.randomUUID());
        DecisionUE ue = new DecisionUE();
        deliberation.ajouterDecisionUE(ue);
        assertEquals(deliberation.getId(), ue.getDeliberationId());
        assertNotNull(ue.getId());
        assertEquals(1, deliberation.getDecisionsUE().size());
    }

    @Test
    void membresCopiesDefensivement() {
        Deliberation deliberation = new Deliberation();
        var membres = new java.util.ArrayList<UUID>();
        deliberation.setMembres(membres);
        membres.add(UUID.randomUUID());
        assertTrue(deliberation.getMembres().isEmpty());
    }
}
