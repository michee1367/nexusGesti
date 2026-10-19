package com.nexusgesti.boulangerie.production.domain.port.out;

import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;

public interface ProductionEventPublisher {

    void publierPlanValide(PlanProduction plan);
    void publierFourneeValidee(Fournee fournee);
    void publierMouvementStock(MouvementStockIntention intention);
}
