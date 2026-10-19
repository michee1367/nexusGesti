package com.nexusgesti.boulangerie.production.domain.port.in;

import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LigneConsommation;
import com.nexusgesti.boulangerie.production.domain.model.PerteProduction;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductionUseCase {

    PlanProduction creerPlan(PlanProduction plan);
    PlanProduction getPlan(UUID id);
    List<PlanProduction> listPlans();
    PlanProduction validerPlan(UUID id);
    PlanProduction lancerPlan(UUID id);
    PlanProduction cloturerPlan(UUID id);

    Fournee creerFournee(Fournee fournee);
    Fournee getFournee(UUID id);
    List<Fournee> listFournees();
    Fournee lancerFournee(UUID id);
    Fournee enregistrerRendu(UUID id, BigDecimal quantite);
    Fournee cuireFournee(UUID id);
    Fournee validerFournee(UUID id);
    Fournee annulerFournee(UUID id, String motif);
    Fournee declarerConsommation(UUID fourneeId, List<LigneConsommation> lignes);
    Fournee declarerPerte(UUID fourneeId, PerteProduction perte, BigDecimal prixMoyen);
    List<PerteProduction> listPertes(UUID fourneeId);
}
