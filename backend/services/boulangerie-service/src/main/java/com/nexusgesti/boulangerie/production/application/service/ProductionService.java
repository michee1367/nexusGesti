package com.nexusgesti.boulangerie.production.application.service;

import com.nexusgesti.boulangerie.production.domain.ProductionException;
import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LigneConsommation;
import com.nexusgesti.boulangerie.production.domain.model.LignePlan;
import com.nexusgesti.boulangerie.production.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.production.domain.model.PerteProduction;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.model.TypeMouvementStock;
import com.nexusgesti.boulangerie.production.domain.port.in.ProductionUseCase;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionEventPublisher;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionRepository;
import com.nexusgesti.boulangerie.production.domain.port.out.RecetteProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ProductionService implements ProductionUseCase {

    private final ProductionRepository repository;
    private final ProductionEventPublisher eventPublisher;
    private final RecetteProvider recetteProvider;

    public ProductionService(ProductionRepository repository,
                             ProductionEventPublisher eventPublisher,
                             RecetteProvider recetteProvider) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.recetteProvider = recetteProvider;
    }

    // ---------- PLAN ----------

    @Override
    public PlanProduction creerPlan(PlanProduction plan) {
        if (plan.getLignes().isEmpty()) {
            throw ProductionException.invalide("Un plan doit porter au moins une ligne");
        }
        if (plan.getIdPlan() == null) {
            plan.setIdPlan(UUID.randomUUID());
        }
        plan.getLignes().forEach(ligne -> ligne.setPlanId(plan.getIdPlan()));
        plan.planifier();
        plan.definirBesoins(agregerBesoins(plan.getLignes()));
        return repository.savePlan(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanProduction getPlan(UUID id) {
        return loadPlan(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanProduction> listPlans() {
        return repository.listPlans();
    }

    @Override
    public PlanProduction validerPlan(UUID id) {
        PlanProduction plan = loadPlan(id);
        plan.valider();
        PlanProduction saved = repository.savePlan(plan);
        eventPublisher.publierPlanValide(saved);
        return saved;
    }

    @Override
    public PlanProduction lancerPlan(UUID id) {
        PlanProduction plan = loadPlan(id);
        plan.lancer();
        return repository.savePlan(plan);
    }

    @Override
    public PlanProduction cloturerPlan(UUID id) {
        PlanProduction plan = loadPlan(id);
        plan.cloturer();
        return repository.savePlan(plan);
    }

    // ---------- FOURNEE ----------

    @Override
    public Fournee creerFournee(Fournee fournee) {
        if (fournee.getIdFournee() == null) {
            fournee.setIdFournee(UUID.randomUUID());
        }
        if (fournee.getNumero() == null || fournee.getNumero().isBlank()) {
            fournee.setNumero("FOU-" + String.format("%05d", repository.countFournees() + 1));
        }
        return repository.saveFournee(fournee);
    }

    @Override
    @Transactional(readOnly = true)
    public Fournee getFournee(UUID id) {
        return loadFournee(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fournee> listFournees() {
        return repository.listFournees();
    }

    @Override
    public Fournee lancerFournee(UUID id) {
        Fournee fournee = loadFournee(id);
        fournee.lancer();
        return repository.saveFournee(fournee);
    }

    @Override
    public Fournee enregistrerRendu(UUID id, BigDecimal quantite) {
        Fournee fournee = loadFournee(id);
        fournee.enregistrerRendu(quantite);
        return repository.saveFournee(fournee);
    }

    @Override
    public Fournee cuireFournee(UUID id) {
        Fournee fournee = loadFournee(id);
        fournee.cuire();
        return repository.saveFournee(fournee);
    }

    @Override
    public Fournee validerFournee(UUID id) {
        Fournee fournee = loadFournee(id);
        fournee.valider();
        Fournee saved = repository.saveFournee(fournee);
        eventPublisher.publierFourneeValidee(saved);
        publierEntreeProduction(saved);
        for (PerteProduction perte : saved.getPertes()) {
            eventPublisher.publierMouvementStock(sortiePerte(perte));
        }
        return saved;
    }

    @Override
    public Fournee annulerFournee(UUID id, String motif) {
        Fournee fournee = loadFournee(id);
        fournee.annuler(motif);
        return repository.saveFournee(fournee);
    }

    @Override
    public Fournee declarerConsommation(UUID fourneeId, List<LigneConsommation> lignes) {
        Fournee fournee = loadFournee(fourneeId);
        if (lignes == null || lignes.isEmpty()) {
            throw ProductionException.invalide("Aucune consommation a declarer");
        }
        for (LigneConsommation ligne : lignes) {
            fournee.declarerConsommation(ligne);
        }
        Fournee saved = repository.saveFournee(fournee);
        for (LigneConsommation ligne : saved.getConsommations()) {
            eventPublisher.publierMouvementStock(sortieConsommation(ligne));
        }
        return saved;
    }

    @Override
    public Fournee declarerPerte(UUID fourneeId, PerteProduction perte, BigDecimal prixMoyen) {
        Fournee fournee = loadFournee(fourneeId);
        if (perte == null) {
            throw ProductionException.invalide("Perte absente");
        }
        if (perte.getTypePerte() == null) {
            throw ProductionException.invalide("Le type de perte est obligatoire");
        }
        perte.valoriser(prixMoyen);
        fournee.declarerPerte(perte);
        return repository.saveFournee(fournee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerteProduction> listPertes(UUID fourneeId) {
        return loadFournee(fourneeId).getPertes();
    }

    // ---------- HELPERS ----------

    private List<BesoinMatiere> agregerBesoins(List<LignePlan> lignes) {
        Map<String, BesoinMatiere> cumul = new LinkedHashMap<>();
        for (LignePlan ligne : lignes) {
            BigDecimal quantite = ligne.getQuantitePrevue() == null ? BigDecimal.ZERO : ligne.getQuantitePrevue();
            List<BesoinMatiere> besoins =
                    recetteProvider.resoudreBesoins(ligne.getRecetteCode(), ligne.getVersionRecette(), quantite);
            for (BesoinMatiere besoin : besoins) {
                cumul.merge(besoin.articleCode(), besoin, (a, b) -> new BesoinMatiere(
                        a.articleCode(),
                        a.libelle(),
                        a.quantiteNette().add(b.quantiteNette()),
                        a.quantiteBrute().add(b.quantiteBrute()),
                        a.unite()));
            }
        }
        return new ArrayList<>(cumul.values());
    }

    private MouvementStockIntention sortieConsommation(LigneConsommation ligne) {
        return new MouvementStockIntention(
                TypeMouvementStock.SORTIE_CONSOMMATION,
                ligne.getArticleCode(),
                ligne.getLotNumero(),
                ligne.getQuantiteReelle(),
                ligne.getPrixMoyen(),
                ligne.getId() == null ? null : ligne.getId().toString());
    }

    private void publierEntreeProduction(Fournee fournee) {
        eventPublisher.publierMouvementStock(new MouvementStockIntention(
                TypeMouvementStock.ENTREE_PRODUCTION,
                fournee.getProduitCode(),
                null,
                fournee.getQuantiteRealisee(),
                BigDecimal.ZERO,
                fournee.getIdFournee().toString()));
    }

    private MouvementStockIntention sortiePerte(PerteProduction perte) {
        BigDecimal prixMoyen = BigDecimal.ZERO;
        if (perte.getQuantite() != null && perte.getQuantite().signum() != 0) {
            prixMoyen = perte.getValorisation().divide(perte.getQuantite(), 4, RoundingMode.HALF_UP);
        }
        return new MouvementStockIntention(
                TypeMouvementStock.SORTIE_PERTE,
                perte.getArticleCode(),
                null,
                perte.getQuantite(),
                prixMoyen,
                perte.getIdPerte() == null ? null : perte.getIdPerte().toString());
    }

    private PlanProduction loadPlan(UUID id) {
        return repository.findPlan(id)
                .orElseThrow(() -> ProductionException.notFound("PlanProduction", id));
    }

    private Fournee loadFournee(UUID id) {
        return repository.findFournee(id)
                .orElseThrow(() -> ProductionException.notFound("Fournee", id));
    }
}
