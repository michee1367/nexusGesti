package com.nexusgesti.boulangerie.production.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.FourneeEntity;
import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.LigneConsommationEntity;
import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.LignePlanEntity;
import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.PerteProductionEntity;
import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.PlanProductionEntity;
import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LigneConsommation;
import com.nexusgesti.boulangerie.production.domain.model.LignePlan;
import com.nexusgesti.boulangerie.production.domain.model.PerteProduction;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.model.StatutFournee;
import com.nexusgesti.boulangerie.production.domain.model.StatutPlan;
import com.nexusgesti.boulangerie.production.domain.model.TypePerte;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductionPersistenceAdapter implements ProductionRepository {

    private final PlanProductionJpaRepository planRepository;
    private final LignePlanJpaRepository lignePlanRepository;
    private final FourneeJpaRepository fourneeRepository;
    private final LigneConsommationJpaRepository consommationRepository;
    private final PerteProductionJpaRepository perteRepository;
    private final ObjectMapper objectMapper;

    public ProductionPersistenceAdapter(PlanProductionJpaRepository planRepository,
                                        LignePlanJpaRepository lignePlanRepository,
                                        FourneeJpaRepository fourneeRepository,
                                        LigneConsommationJpaRepository consommationRepository,
                                        PerteProductionJpaRepository perteRepository,
                                        ObjectMapper objectMapper) {
        this.planRepository = planRepository;
        this.lignePlanRepository = lignePlanRepository;
        this.fourneeRepository = fourneeRepository;
        this.consommationRepository = consommationRepository;
        this.perteRepository = perteRepository;
        this.objectMapper = objectMapper;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- PLAN ----------

    @Override
    @Transactional
    public PlanProduction savePlan(PlanProduction plan) {
        if (plan.getIdPlan() == null) {
            plan.setIdPlan(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        lignePlanRepository.deleteByPlanId(plan.getIdPlan());

        PlanProductionEntity entity = new PlanProductionEntity();
        entity.setId(plan.getIdPlan());
        entity.setDate(plan.getDate());
        entity.setAtelierCode(plan.getAtelierCode());
        entity.setStatut(plan.getStatut().name());
        entity.setBesoinsMatiere(serializeBesoins(plan.getBesoinsMatiere()));
        entity.setTenantId(tenantId);
        planRepository.save(entity);

        for (LignePlan ligne : plan.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            LignePlanEntity ligneEntity = new LignePlanEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setPlanId(plan.getIdPlan());
            ligneEntity.setRecetteCode(ligne.getRecetteCode());
            ligneEntity.setVersionRecette(ligne.getVersionRecette());
            ligneEntity.setQuantitePrevue(ligne.getQuantitePrevue());
            ligneEntity.setProduitCode(ligne.getProduitCode());
            ligneEntity.setFourCode(ligne.getFourCode());
            ligneEntity.setTenantId(tenantId);
            lignePlanRepository.save(ligneEntity);
        }
        return plan;
    }

    @Override
    public Optional<PlanProduction> findPlan(UUID id) {
        return planRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PlanProduction> listPlans() {
        List<PlanProduction> out = new ArrayList<>();
        planRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- FOURNEE ----------

    @Override
    @Transactional
    public Fournee saveFournee(Fournee fournee) {
        if (fournee.getIdFournee() == null) {
            fournee.setIdFournee(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        consommationRepository.deleteByFourneeId(fournee.getIdFournee());
        perteRepository.deleteByFourneeId(fournee.getIdFournee());

        FourneeEntity entity = new FourneeEntity();
        entity.setId(fournee.getIdFournee());
        entity.setNumero(fournee.getNumero());
        entity.setRecetteCode(fournee.getRecetteCode());
        entity.setVersionRecette(fournee.getVersionRecette());
        entity.setProduitCode(fournee.getProduitCode());
        entity.setFourCode(fournee.getFourCode());
        entity.setOperateurId(fournee.getOperateurId());
        entity.setDateDebut(fournee.getDateDebut());
        entity.setDateFin(fournee.getDateFin());
        entity.setQuantitePrevue(fournee.getQuantitePrevue());
        entity.setQuantiteRealisee(fournee.getQuantiteRealisee());
        entity.setStatut(fournee.getStatut().name());
        entity.setTenantId(tenantId);
        fourneeRepository.save(entity);

        for (LigneConsommation ligne : fournee.getConsommations()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            LigneConsommationEntity ligneEntity = new LigneConsommationEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setFourneeId(fournee.getIdFournee());
            ligneEntity.setArticleCode(ligne.getArticleCode());
            ligneEntity.setLotNumero(ligne.getLotNumero());
            ligneEntity.setQuantiteTheorique(ligne.getQuantiteTheorique());
            ligneEntity.setQuantiteReelle(ligne.getQuantiteReelle());
            ligneEntity.setPrixMoyen(ligne.getPrixMoyen());
            ligneEntity.setTenantId(tenantId);
            consommationRepository.save(ligneEntity);
        }

        for (PerteProduction perte : fournee.getPertes()) {
            if (perte.getIdPerte() == null) {
                perte.setIdPerte(UUID.randomUUID());
            }
            PerteProductionEntity perteEntity = new PerteProductionEntity();
            perteEntity.setId(perte.getIdPerte());
            perteEntity.setFourneeId(fournee.getIdFournee());
            perteEntity.setArticleCode(perte.getArticleCode());
            perteEntity.setQuantite(perte.getQuantite());
            perteEntity.setTypePerte(perte.getTypePerte() == null ? null : perte.getTypePerte().name());
            perteEntity.setMotif(perte.getMotif());
            perteEntity.setValorisation(perte.getValorisation());
            perteEntity.setTenantId(tenantId);
            perteRepository.save(perteEntity);
        }
        return fournee;
    }

    @Override
    public Optional<Fournee> findFournee(UUID id) {
        return fourneeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Fournee> listFournees() {
        List<Fournee> out = new ArrayList<>();
        fourneeRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    @Override
    public int countFournees() {
        return (int) fourneeRepository.count();
    }

    // ---------- MAPPING ----------

    private PlanProduction toDomain(PlanProductionEntity entity) {
        PlanProduction plan = new PlanProduction();
        plan.setIdPlan(entity.getId());
        plan.setDate(entity.getDate());
        plan.setAtelierCode(entity.getAtelierCode());
        plan.setStatut(entity.getStatut() == null ? null : StatutPlan.valueOf(entity.getStatut()));
        plan.setBesoinsMatiere(deserializeBesoins(entity.getBesoinsMatiere()));

        List<LignePlan> lignes = new ArrayList<>();
        for (LignePlanEntity ligne : lignePlanRepository.findByPlanId(entity.getId())) {
            LignePlan model = new LignePlan();
            model.setId(ligne.getId());
            model.setPlanId(ligne.getPlanId());
            model.setRecetteCode(ligne.getRecetteCode());
            model.setVersionRecette(ligne.getVersionRecette());
            model.setQuantitePrevue(ligne.getQuantitePrevue());
            model.setProduitCode(ligne.getProduitCode());
            model.setFourCode(ligne.getFourCode());
            lignes.add(model);
        }
        plan.setLignes(lignes);
        return plan;
    }

    private Fournee toDomain(FourneeEntity entity) {
        Fournee fournee = new Fournee();
        fournee.setIdFournee(entity.getId());
        fournee.setNumero(entity.getNumero());
        fournee.setRecetteCode(entity.getRecetteCode());
        fournee.setVersionRecette(entity.getVersionRecette());
        fournee.setProduitCode(entity.getProduitCode());
        fournee.setFourCode(entity.getFourCode());
        fournee.setOperateurId(entity.getOperateurId());
        fournee.setDateDebut(entity.getDateDebut());
        fournee.setDateFin(entity.getDateFin());
        fournee.setQuantitePrevue(entity.getQuantitePrevue());
        fournee.setQuantiteRealisee(entity.getQuantiteRealisee());
        fournee.setStatut(entity.getStatut() == null ? null : StatutFournee.valueOf(entity.getStatut()));

        List<LigneConsommation> consommations = new ArrayList<>();
        for (LigneConsommationEntity ligne : consommationRepository.findByFourneeId(entity.getId())) {
            LigneConsommation model = new LigneConsommation();
            model.setId(ligne.getId());
            model.setFourneeId(ligne.getFourneeId());
            model.setArticleCode(ligne.getArticleCode());
            model.setLotNumero(ligne.getLotNumero());
            model.setQuantiteTheorique(ligne.getQuantiteTheorique());
            model.setQuantiteReelle(ligne.getQuantiteReelle());
            model.setPrixMoyen(ligne.getPrixMoyen());
            consommations.add(model);
        }
        fournee.setConsommations(consommations);

        List<PerteProduction> pertes = new ArrayList<>();
        for (PerteProductionEntity perte : perteRepository.findByFourneeId(entity.getId())) {
            PerteProduction model = new PerteProduction();
            model.setIdPerte(perte.getId());
            model.setFourneeId(perte.getFourneeId());
            model.setArticleCode(perte.getArticleCode());
            model.setQuantite(perte.getQuantite());
            model.setTypePerte(perte.getTypePerte() == null ? null : TypePerte.valueOf(perte.getTypePerte()));
            model.setMotif(perte.getMotif());
            model.setValorisation(perte.getValorisation());
            pertes.add(model);
        }
        fournee.setPertes(pertes);
        return fournee;
    }

    private String serializeBesoins(List<BesoinMatiere> besoins) {
        if (besoins == null || besoins.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(besoins);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Serialisation des besoins impossible", e);
        }
    }

    private List<BesoinMatiere> deserializeBesoins(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<BesoinMatiere>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Deserialisation des besoins impossible", e);
        }
    }
}
