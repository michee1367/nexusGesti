package com.nexusgesti.boulangerie.production.domain.port.out;

import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionRepository {

    PlanProduction savePlan(PlanProduction plan);
    Optional<PlanProduction> findPlan(UUID id);
    List<PlanProduction> listPlans();

    Fournee saveFournee(Fournee fournee);
    Optional<Fournee> findFournee(UUID id);
    List<Fournee> listFournees();

    int countFournees();
}
