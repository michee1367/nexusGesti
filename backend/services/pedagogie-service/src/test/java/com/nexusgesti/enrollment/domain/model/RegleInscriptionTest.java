package com.nexusgesti.enrollment.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegleInscriptionTest {

    private Candidat candidat(BigDecimal credits, BigDecimal moyenne, Integer age, List<String> niveaux) {
        Candidat c = new Candidat();
        c.setEleveId(UUID.randomUUID());
        c.setCreditsValides(credits);
        c.setMoyenneGenerale(moyenne);
        c.setAge(age);
        c.setNiveauxValides(niveaux);
        return c;
    }

    @Test
    void candidatSansContrainteEstEligible() {
        RegleInscription r = new RegleInscription();
        r.setLibelle("Aucune contrainte");
        assertEquals(DecisionInscription.ELIGIBLE,
                r.evaluer(candidat(null, null, null, List.of())));
    }

    @Test
    void creditsInsuffisantsRendentIneligible() {
        RegleInscription r = new RegleInscription();
        r.setCreditsMin(new BigDecimal("60"));
        assertEquals(DecisionInscription.INELIGIBLE,
                r.evaluer(candidat(new BigDecimal("30"), null, null, List.of())));
    }

    @Test
    void moyenneInsuffisanteRendIneligible() {
        RegleInscription r = new RegleInscription();
        r.setMoyenneMin(new BigDecimal("10"));
        assertEquals(DecisionInscription.INELIGIBLE,
                r.evaluer(candidat(null, new BigDecimal("8"), null, List.of())));
    }

    @Test
    void ageAuDessusDuCreneauRendIneligible() {
        RegleInscription r = new RegleInscription();
        r.setCreneauAge(25);
        assertEquals(DecisionInscription.INELIGIBLE,
                r.evaluer(candidat(null, null, 30, List.of())));
    }

    @Test
    void conditionNonRemplieRendIneligible() {
        RegleInscription r = new RegleInscription();
        r.setCondition("BAC");
        assertEquals(DecisionInscription.INELIGIBLE,
                r.evaluer(candidat(null, null, null, List.of("BEPC"))));
    }

    @Test
    void conditionRemplieEstEligible() {
        RegleInscription r = new RegleInscription();
        r.setCondition("bac");
        assertEquals(DecisionInscription.ELIGIBLE,
                r.evaluer(candidat(null, null, null, List.of("BAC"))));
    }

    @Test
    void regleGeneraleSAppliqueATousLesProgrammes() {
        RegleInscription r = new RegleInscription();
        assertTrue(r.sAppliqueA(UUID.randomUUID()));
        assertTrue(r.sAppliqueA(null));
    }

    @Test
    void regleSpecifiqueNeSAppliqueQuaSonProgramme() {
        UUID programme = UUID.randomUUID();
        RegleInscription r = new RegleInscription();
        r.setProgrammeId(programme);
        assertTrue(r.sAppliqueA(programme));
        assertTrue(!r.sAppliqueA(UUID.randomUUID()));
    }
}
