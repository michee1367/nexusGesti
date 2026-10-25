package com.nexusgesti.boulangerie.production.domain.model;

import com.nexusgesti.boulangerie.production.domain.ProductionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlanProductionTest {

    private PlanProduction plan() {
        PlanProduction plan = new PlanProduction();
        plan.setDate(LocalDate.of(2026, 10, 17));
        return plan;
    }

    @Test
    void planifierSansDateEchoue() {
        PlanProduction plan = new PlanProduction();
        assertThrows(ProductionException.class, plan::planifier);
    }

    @Test
    void planNouveauEstBrouillon() {
        PlanProduction plan = plan().planifier();
        assertEquals(StatutPlan.BROUILLON, plan.getStatut());
    }

    @Test
    void transitionsNominales() {
        PlanProduction plan = plan().planifier();
        plan.valider();
        assertEquals(StatutPlan.VALIDE, plan.getStatut());
        plan.lancer();
        assertEquals(StatutPlan.LANCE, plan.getStatut());
        plan.cloturer();
        assertEquals(StatutPlan.CLOTURE, plan.getStatut());
    }

    @Test
    void validerDepuisLanceEchoue() {
        PlanProduction plan = plan().planifier();
        plan.valider();
        assertThrows(ProductionException.class, plan::valider);
    }

    @Test
    void estEquilibreVraiSiStockSuffisant() {
        PlanProduction plan = plan();
        plan.definirBesoins(List.of(new BesoinMatiere("FARINE", "Farine", BigDecimal.TEN, BigDecimal.TEN, "KG")));
        assertTrue(plan.estEquilibre(Map.of("FARINE", BigDecimal.valueOf(20))));
        assertFalse(plan.estEquilibre(Map.of("FARINE", BigDecimal.ONE)));
    }

    @Test
    void estEquilibreVraiSansBesoin() {
        assertTrue(plan().estEquilibre(Map.of()));
    }
}
