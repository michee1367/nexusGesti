package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.fees.adapter.out.persistence.entity.*;
import com.nexusgesti.fees.domain.model.*;
import com.nexusgesti.fees.domain.port.out.FeesRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FeesPersistenceAdapter implements FeesRepository {

    private final GrilleTarifaireJpaRepository grilleRepository;
    private final LigneGrilleJpaRepository ligneGrilleRepository;
    private final ReductionJpaRepository reductionRepository;
    private final ConditionReductionJpaRepository conditionRepository;
    private final AvisPaiementJpaRepository avisRepository;
    private final LigneFraisJpaRepository ligneFraisRepository;
    private final EcheanceJpaRepository echeanceRepository;
    private final PaiementJpaRepository paiementRepository;
    private final ImputationJpaRepository imputationRepository;
    private final PaiementMobileMoneyJpaRepository mobileMoneyRepository;
    private final PaiementVirementJpaRepository virementRepository;
    private final PaiementEspecesJpaRepository especesRepository;
    private final RemboursementJpaRepository remboursementRepository;
    private final CouponPaiementJpaRepository couponRepository;
    private final JournalierJpaRepository journalierRepository;

    public FeesPersistenceAdapter(GrilleTarifaireJpaRepository grilleRepository,
                                  LigneGrilleJpaRepository ligneGrilleRepository,
                                  ReductionJpaRepository reductionRepository,
                                  ConditionReductionJpaRepository conditionRepository,
                                  AvisPaiementJpaRepository avisRepository,
                                  LigneFraisJpaRepository ligneFraisRepository,
                                  EcheanceJpaRepository echeanceRepository,
                                  PaiementJpaRepository paiementRepository,
                                  ImputationJpaRepository imputationRepository,
                                  PaiementMobileMoneyJpaRepository mobileMoneyRepository,
                                  PaiementVirementJpaRepository virementRepository,
                                  PaiementEspecesJpaRepository especesRepository,
                                  RemboursementJpaRepository remboursementRepository,
                                  CouponPaiementJpaRepository couponRepository,
                                  JournalierJpaRepository journalierRepository) {
        this.grilleRepository = grilleRepository;
        this.ligneGrilleRepository = ligneGrilleRepository;
        this.reductionRepository = reductionRepository;
        this.conditionRepository = conditionRepository;
        this.avisRepository = avisRepository;
        this.ligneFraisRepository = ligneFraisRepository;
        this.echeanceRepository = echeanceRepository;
        this.paiementRepository = paiementRepository;
        this.imputationRepository = imputationRepository;
        this.mobileMoneyRepository = mobileMoneyRepository;
        this.virementRepository = virementRepository;
        this.especesRepository = especesRepository;
        this.remboursementRepository = remboursementRepository;
        this.couponRepository = couponRepository;
        this.journalierRepository = journalierRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- GRILLE TARIFAIRE ----------

    @Override
    @Transactional
    public GrilleTarifaire saveGrille(GrilleTarifaire grille) {
        if (grille.getId() == null) {
            grille.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneGrilleRepository.deleteByGrilleId(grille.getId());
        for (ReductionEntity existing : reductionRepository.findByGrilleId(grille.getId())) {
            conditionRepository.deleteByReductionId(existing.getId());
        }
        reductionRepository.deleteByGrilleId(grille.getId());
        grilleRepository.save(grilleEntity(grille, tenantId));

        for (LigneGrille ligne : grille.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setGrilleId(grille.getId());
            ligneGrilleRepository.save(ligneGrilleEntity(ligne, tenantId));
        }
        for (Reduction reduction : grille.getReductions()) {
            saveReduction(reduction, tenantId);
        }
        return findGrille(grille.getId()).orElse(grille);
    }

    @Override
    public Optional<GrilleTarifaire> findGrille(UUID id) {
        return grilleRepository.findById(id).map(this::grilleToDomain);
    }

    @Override
    public List<GrilleTarifaire> listGrilles(UUID anneeId, String profil) {
        List<GrilleTarifaire> grilles = grilleRepository.findAll().stream()
                .map(this::grilleToDomain).toList();
        return grilles.stream()
                .filter(g -> anneeId == null || anneeId.equals(g.getAnnee()))
                .filter(g -> profil == null || profil.isBlank() || profil.equalsIgnoreCase(g.getProfil()))
                .toList();
    }

    private GrilleTarifaireEntity grilleEntity(GrilleTarifaire d, UUID tenantId) {
        GrilleTarifaireEntity e = new GrilleTarifaireEntity();
        e.setId(d.getId());
        e.setCode(d.getCode());
        e.setLibelle(d.getLibelle());
        e.setAnnee(d.getAnnee());
        e.setProfil(d.getProfil());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setDevise(d.getDevise());
        e.setDateActivation(d.getDateActivation());
        e.setActive(d.isActive());
        e.setVersion(d.getVersion());
        e.setModifiable(d.isModifiable());
        e.setTenantId(tenantId);
        return e;
    }

    private GrilleTarifaire grilleToDomain(GrilleTarifaireEntity e) {
        GrilleTarifaire d = new GrilleTarifaire();
        d.setId(e.getId());
        d.setCode(e.getCode());
        d.setLibelle(e.getLibelle());
        d.setAnnee(e.getAnnee());
        d.setProfil(e.getProfil());
        d.setStatut(e.getStatut() == null ? null : StatutGrille.valueOf(e.getStatut()));
        d.setDevise(e.getDevise());
        d.setDateActivation(e.getDateActivation());
        d.setActive(e.isActive());
        d.setVersion(e.getVersion());
        d.setModifiable(e.isModifiable());
        d.setLignes(ligneGrilleRepository.findByGrilleId(e.getId()).stream()
                .map(this::ligneGrilleToDomain).toList());
        d.setReductions(reductionRepository.findByGrilleId(e.getId()).stream()
                .map(this::reductionToDomain).toList());
        return d;
    }

    private LigneGrilleEntity ligneGrilleEntity(LigneGrille d, UUID tenantId) {
        LigneGrilleEntity e = new LigneGrilleEntity();
        e.setId(d.getId());
        e.setGrilleId(d.getGrilleId());
        e.setTypeFrais(d.getTypeFrais() == null ? null : d.getTypeFrais().name());
        e.setCategorie(d.getCategorie() == null ? null : d.getCategorie().name());
        e.setLibelle(d.getLibelle());
        e.setMontant(d.getMontant());
        e.setMontantPromo(d.getMontantPromo());
        e.setDevise(d.getDevise());
        e.setObligatoire(d.isObligatoire());
        e.setPeriodicite(d.getPeriodicite() == null ? null : d.getPeriodicite().name());
        e.setJour(d.getJour());
        e.setNbEcheances(d.getNbEcheances());
        e.setOrdre(d.getOrdre());
        e.setTenantId(tenantId);
        return e;
    }

    private LigneGrille ligneGrilleToDomain(LigneGrilleEntity e) {
        LigneGrille d = new LigneGrille();
        d.setId(e.getId());
        d.setGrilleId(e.getGrilleId());
        d.setTypeFrais(e.getTypeFrais() == null ? null : TypeFrais.valueOf(e.getTypeFrais()));
        d.setCategorie(e.getCategorie() == null ? null : CategorieFrais.valueOf(e.getCategorie()));
        d.setLibelle(e.getLibelle());
        d.setMontant(e.getMontant());
        d.setMontantPromo(e.getMontantPromo());
        d.setDevise(e.getDevise());
        d.setObligatoire(e.isObligatoire());
        d.setPeriodicite(e.getPeriodicite() == null ? null : Periodicite.valueOf(e.getPeriodicite()));
        d.setJour(e.getJour());
        d.setNbEcheances(e.getNbEcheances());
        d.setOrdre(e.getOrdre());
        return d;
    }

    private void saveReduction(Reduction d, UUID tenantId) {
        if (d.getId() == null) {
            d.setId(UUID.randomUUID());
        }
        conditionRepository.deleteByReductionId(d.getId());
        reductionRepository.save(reductionEntity(d, tenantId));
        for (ConditionReduction condition : d.getConditions()) {
            ConditionReductionEntity c = conditionEntity(condition, tenantId);
            c.setId(UUID.randomUUID());
            c.setReductionId(d.getId());
            conditionRepository.save(c);
        }
    }

    private ReductionEntity reductionEntity(Reduction d, UUID tenantId) {
        ReductionEntity e = new ReductionEntity();
        e.setId(d.getId());
        e.setGrilleId(d.getGrilleId());
        e.setCode(d.getCode());
        e.setLibelle(d.getLibelle());
        e.setType(d.getType() == null ? null : d.getType().name());
        e.setValeur(d.getValeur());
        e.setCumulable(d.isCumulable());
        e.setTenantId(tenantId);
        return e;
    }

    private Reduction reductionToDomain(ReductionEntity e) {
        Reduction d = new Reduction();
        d.setId(e.getId());
        d.setGrilleId(e.getGrilleId());
        d.setCode(e.getCode());
        d.setLibelle(e.getLibelle());
        d.setType(e.getType() == null ? null : TypeReduction.valueOf(e.getType()));
        d.setValeur(e.getValeur());
        d.setCumulable(e.isCumulable());
        d.setConditions(conditionRepository.findByReductionId(e.getId()).stream()
                .map(this::conditionToDomain).toList());
        return d;
    }

    private ConditionReductionEntity conditionEntity(ConditionReduction d, UUID tenantId) {
        ConditionReductionEntity e = new ConditionReductionEntity();
        e.setCritere(d.getCritere());
        e.setOperateur(d.getOperateur());
        e.setValeur(d.getValeur());
        e.setCumulable(d.isCumulable());
        e.setTenantId(tenantId);
        return e;
    }

    private ConditionReduction conditionToDomain(ConditionReductionEntity e) {
        ConditionReduction d = new ConditionReduction();
        d.setCritere(e.getCritere());
        d.setOperateur(e.getOperateur());
        d.setValeur(e.getValeur());
        d.setCumulable(e.isCumulable());
        return d;
    }

    // ---------- AVIS DE PAIEMENT ----------

    @Override
    @Transactional
    public AvisPaiement saveAvis(AvisPaiement avis) {
        if (avis.getIdAvis() == null) {
            avis.setIdAvis(UUID.randomUUID());
            avis.setReference(reference("AV-"));
        }
        UUID tenantId = tenant();
        avisRepository.save(avisEntity(avis, tenantId));

        for (LigneFrais ligne : avis.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setAvisId(avis.getIdAvis());
            saveLigneFrais(ligne, tenantId);
        }
        return findAvis(avis.getIdAvis()).orElse(avis);
    }

    @Override
    public Optional<AvisPaiement> findAvis(UUID id) {
        return avisRepository.findById(id).map(this::avisToDomain);
    }

    @Override
    public List<AvisPaiement> listAvis(UUID eleveId, UUID anneeId) {
        return avisRepository.findAll().stream()
                .map(this::avisToDomain)
                .filter(a -> eleveId == null || eleveId.equals(a.getEleve()))
                .filter(a -> anneeId == null || anneeId.equals(a.getAnnee()))
                .toList();
    }

    public String reference(String prefix) {
        return prefix + java.time.LocalDate.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AvisPaiementEntity avisEntity(AvisPaiement d, UUID tenantId) {
        AvisPaiementEntity e = new AvisPaiementEntity();
        e.setId(d.getIdAvis());
        e.setReference(d.getReference());
        e.setEleve(d.getEleve());
        e.setAnnee(d.getAnnee());
        e.setClasse(d.getClasse());
        e.setGrilleId(d.getGrilleId());
        e.setDateEmission(d.getDateEmission());
        e.setDateEcheanceGlobale(d.getDateEcheanceGlobale());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setMontantBrut(d.getMontantBrut());
        e.setMontantReduction(d.getMontantReduction());
        e.setMontantNet(d.getMontantNet());
        e.setMontantRegle(d.getMontantRegle());
        e.setSoldeDu(d.getSoldeDu());
        e.setDevise(d.getDevise());
        e.setTauxChange(d.getTauxChange());
        e.setJournalier(d.getJournalier());
        e.setEcritureGeneree(d.getEcritureGeneree());
        e.setMotifAnnulation(d.getMotifAnnulation());
        e.setTenantId(tenantId);
        return e;
    }

    private AvisPaiement avisToDomain(AvisPaiementEntity e) {
        AvisPaiement d = new AvisPaiement();
        d.setIdAvis(e.getId());
        d.setReference(e.getReference());
        d.setEleve(e.getEleve());
        d.setAnnee(e.getAnnee());
        d.setClasse(e.getClasse());
        d.setGrilleId(e.getGrilleId());
        d.setDateEmission(e.getDateEmission());
        d.setDateEcheanceGlobale(e.getDateEcheanceGlobale());
        d.setStatut(e.getStatut() == null ? null : StatutAvis.valueOf(e.getStatut()));
        d.setMontantBrut(e.getMontantBrut());
        d.setMontantReduction(e.getMontantReduction());
        d.setMontantNet(e.getMontantNet());
        d.setMontantRegle(e.getMontantRegle());
        d.setSoldeDu(e.getSoldeDu());
        d.setDevise(e.getDevise());
        d.setTauxChange(e.getTauxChange());
        d.setJournalier(e.getJournalier());
        d.setEcritureGeneree(e.getEcritureGeneree());
        d.setMotifAnnulation(e.getMotifAnnulation());
        d.setLignes(ligneFraisRepository.findByAvisId(e.getId()).stream()
                .map(this::ligneFraisToDomain).toList());
        return d;
    }

    private void saveLigneFrais(LigneFrais d, UUID tenantId) {
        echeanceRepository.deleteByLigneId(d.getId());
        ligneFraisRepository.save(ligneFraisEntity(d, tenantId));
        for (Echeance echeance : d.getEcheances()) {
            if (echeance.getId() == null) {
                echeance.setId(UUID.randomUUID());
            }
            echeance.setLigneId(d.getId());
            echeanceRepository.save(echeanceEntity(echeance, tenantId));
        }
    }

    private LigneFraisEntity ligneFraisEntity(LigneFrais d, UUID tenantId) {
        LigneFraisEntity e = new LigneFraisEntity();
        e.setId(d.getId());
        e.setAvisId(d.getAvisId());
        e.setTypeFrais(d.getTypeFrais() == null ? null : d.getTypeFrais().name());
        e.setLibelle(d.getLibelle());
        e.setMontantBrut(d.getMontantBrut());
        e.setTauxReduction(d.getTauxReduction());
        e.setMontantReduction(d.getMontantReduction());
        e.setMontantNet(d.getMontantNet());
        e.setNbEcheances(d.getNbEcheances());
        e.setAnneeReference(d.getAnneeReference());
        e.setTenantId(tenantId);
        return e;
    }

    private LigneFrais ligneFraisToDomain(LigneFraisEntity e) {
        LigneFrais d = new LigneFrais();
        d.setId(e.getId());
        d.setAvisId(e.getAvisId());
        d.setTypeFrais(e.getTypeFrais() == null ? null : TypeFrais.valueOf(e.getTypeFrais()));
        d.setLibelle(e.getLibelle());
        d.setMontantBrut(e.getMontantBrut());
        d.setTauxReduction(e.getTauxReduction());
        d.setMontantReduction(e.getMontantReduction());
        d.setMontantNet(e.getMontantNet());
        d.setNbEcheances(e.getNbEcheances());
        d.setAnneeReference(e.getAnneeReference());
        d.setEcheances(echeanceRepository.findByLigneId(e.getId()).stream()
                .map(this::echeanceToDomain).toList());
        return d;
    }

    private EcheanceEntity echeanceEntity(Echeance d, UUID tenantId) {
        EcheanceEntity e = new EcheanceEntity();
        e.setId(d.getId());
        e.setLigneId(d.getLigneId());
        e.setNumero(d.getNumero());
        e.setDateEcheance(d.getDateEcheance());
        e.setMontant(d.getMontant());
        e.setMontantRegle(d.getMontantRegle());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setDateReglement(d.getDateReglement());
        e.setTenantId(tenantId);
        return e;
    }

    private Echeance echeanceToDomain(EcheanceEntity e) {
        Echeance d = new Echeance();
        d.setId(e.getId());
        d.setLigneId(e.getLigneId());
        d.setNumero(e.getNumero());
        d.setDateEcheance(e.getDateEcheance());
        d.setMontant(e.getMontant());
        d.setMontantRegle(e.getMontantRegle());
        d.setStatut(e.getStatut() == null ? null : StatutEcheance.valueOf(e.getStatut()));
        d.setDateReglement(e.getDateReglement());
        return d;
    }

    // ---------- PAIEMENT ----------

    @Override
    @Transactional
    public Paiement savePaiement(Paiement paiement) {
        if (paiement.getIdPaiement() == null) {
            paiement.setIdPaiement(UUID.randomUUID());
            paiement.setReference(reference("PAY-"));
        }
        UUID tenantId = tenant();
        paiementRepository.save(paiementEntity(paiement, tenantId));

        for (Imputation imputation : paiement.getImputations()) {
            if (imputation.getEcheanceId() == null) {
                continue;
            }
            ImputationEntity e = imputationEntity(imputation, tenantId);
            e.setId(UUID.randomUUID());
            e.setPaiementId(paiement.getIdPaiement());
            imputationRepository.save(e);
        }
        if (paiement.getMobileMoney() != null) {
            PaiementMobileMoneyEntity e = mobileMoneyEntity(paiement.getMobileMoney(), tenantId);
            e.setId(UUID.randomUUID());
            e.setPaiementId(paiement.getIdPaiement());
            mobileMoneyRepository.save(e);
        }
        if (paiement.getVirement() != null) {
            PaiementVirementEntity e = virementEntity(paiement.getVirement(), tenantId);
            e.setId(UUID.randomUUID());
            e.setPaiementId(paiement.getIdPaiement());
            virementRepository.save(e);
        }
        if (paiement.getEspeces() != null) {
            PaiementEspecesEntity e = especesEntity(paiement.getEspeces(), tenantId);
            e.setId(UUID.randomUUID());
            e.setPaiementId(paiement.getIdPaiement());
            especesRepository.save(e);
        }
        if (paiement.getRemboursement() != null) {
            RemboursementEntity e = remboursementEntity(paiement.getRemboursement(), tenantId);
            e.setId(UUID.randomUUID());
            e.setPaiementId(paiement.getIdPaiement());
            remboursementRepository.save(e);
        }
        return findPaiement(paiement.getIdPaiement()).orElse(paiement);
    }

    @Override
    public Optional<Paiement> findPaiement(UUID id) {
        return paiementRepository.findById(id).map(this::paiementToDomain);
    }

    @Override
    public List<Paiement> listPaiements(UUID eleveId, UUID avisId) {
        return paiementRepository.findAll().stream()
                .map(this::paiementToDomain)
                .filter(p -> eleveId == null || eleveId.equals(p.getEleve()))
                .filter(p -> avisId == null || avisId.equals(p.getAvis()))
                .toList();
    }

    private PaiementEntity paiementEntity(Paiement d, UUID tenantId) {
        PaiementEntity e = new PaiementEntity();
        e.setId(d.getIdPaiement());
        e.setReference(d.getReference());
        e.setAvisId(d.getAvis());
        e.setEleve(d.getEleve());
        e.setDate(d.getDate());
        e.setMontant(d.getMontant());
        e.setMoyen(d.getMoyen() == null ? null : d.getMoyen().name());
        e.setCompteDebit(d.getCompteDebit());
        e.setReferenceTransaction(d.getReferenceTransaction());
        e.setIdEvenement(d.getIdEvenement());
        e.setBeneficiaire(d.getBeneficiaire() == null ? null : d.getBeneficiaire().name());
        e.setReferenceBeneficiaire(d.getReferenceBeneficiaire());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setEncaissePar(d.getEncaissePar());
        e.setResteARembourser(d.getResteARembourser());
        e.setCouponGenere(d.getCouponGenere());
        e.setEcritureGeneree(d.getEcritureGeneree());
        e.setMotifAnnulation(d.getMotifAnnulation());
        e.setMotifRejet(d.getMotifRejet());
        e.setDateValidation(d.getDateValidation());
        e.setTenantId(tenantId);
        return e;
    }

    private Paiement paiementToDomain(PaiementEntity e) {
        Paiement d = new Paiement();
        d.setIdPaiement(e.getId());
        d.setReference(e.getReference());
        d.setAvis(e.getAvisId());
        d.setEleve(e.getEleve());
        d.setDate(e.getDate());
        d.setMontant(e.getMontant());
        d.setMoyen(e.getMoyen() == null ? null : MoyenPaiement.valueOf(e.getMoyen()));
        d.setCompteDebit(e.getCompteDebit());
        d.setReferenceTransaction(e.getReferenceTransaction());
        d.setIdEvenement(e.getIdEvenement());
        d.setBeneficiaire(e.getBeneficiaire() == null ? null : TypeBeneficiaire.valueOf(e.getBeneficiaire()));
        d.setReferenceBeneficiaire(e.getReferenceBeneficiaire());
        d.setStatut(e.getStatut() == null ? null : StatutPaiement.valueOf(e.getStatut()));
        d.setEncaissePar(e.getEncaissePar());
        d.setResteARembourser(e.getResteARembourser());
        d.setCouponGenere(e.getCouponGenere());
        d.setEcritureGeneree(e.getEcritureGeneree());
        d.setMotifAnnulation(e.getMotifAnnulation());
        d.setMotifRejet(e.getMotifRejet());
        d.setDateValidation(e.getDateValidation());
        d.setImputations(imputationRepository.findByPaiementId(e.getId()).stream()
                .map(this::imputationToDomain).toList());
        mobileMoneyRepository.findByPaiementId(e.getId()).ifPresent(m ->
                d.setMobileMoney(mobileMoneyToDomain(m)));
        virementRepository.findByPaiementId(e.getId()).ifPresent(v ->
                d.setVirement(virementToDomain(v)));
        especesRepository.findByPaiementId(e.getId()).ifPresent(s ->
                d.setEspeces(especesToDomain(s)));
        remboursementRepository.findByPaiementId(e.getId()).ifPresent(r ->
                d.setRemboursement(remboursementToDomain(r)));
        return d;
    }

    private ImputationEntity imputationEntity(Imputation d, UUID tenantId) {
        ImputationEntity e = new ImputationEntity();
        e.setPaiementId(d.getPaiementId());
        e.setEcheanceId(d.getEcheanceId());
        e.setMontant(d.getMontant());
        e.setDateImputation(d.getDateImputation());
        e.setOrdre(d.getOrdre());
        e.setTenantId(tenantId);
        return e;
    }

    private Imputation imputationToDomain(ImputationEntity e) {
        Imputation d = new Imputation();
        d.setPaiementId(e.getPaiementId());
        d.setEcheanceId(e.getEcheanceId());
        d.setMontant(e.getMontant());
        d.setDateImputation(e.getDateImputation());
        d.setOrdre(e.getOrdre());
        return d;
    }

    private PaiementMobileMoneyEntity mobileMoneyEntity(PayementMobileMoney d, UUID tenantId) {
        PaiementMobileMoneyEntity e = new PaiementMobileMoneyEntity();
        e.setOperateur(d.getOperateur());
        e.setNumeroTelephone(d.getNumeroTelephone());
        e.setReferenceTransaction(d.getReferenceTransaction());
        e.setDateTransaction(d.getDateTransaction());
        e.setStatutTransaction(d.getStatutTransaction());
        e.setFrais(d.getFrais());
        e.setMontantRecu(d.getMontantRecu());
        e.setTenantId(tenantId);
        return e;
    }

    private PayementMobileMoney mobileMoneyToDomain(PaiementMobileMoneyEntity e) {
        PayementMobileMoney d = new PayementMobileMoney();
        d.setOperateur(e.getOperateur());
        d.setNumeroTelephone(e.getNumeroTelephone());
        d.setReferenceTransaction(e.getReferenceTransaction());
        d.setDateTransaction(e.getDateTransaction());
        d.setStatutTransaction(e.getStatutTransaction());
        d.setFrais(e.getFrais());
        d.setMontantRecu(e.getMontantRecu());
        return d;
    }

    private PaiementVirementEntity virementEntity(PayementVirement d, UUID tenantId) {
        PaiementVirementEntity e = new PaiementVirementEntity();
        e.setBanque(d.getBanque());
        e.setNumeroCompte(d.getNumeroCompte());
        e.setReferenceVirement(d.getReferenceVirement());
        e.setDateValeur(d.getDateValeur());
        e.setCommission(d.getCommission());
        e.setTenantId(tenantId);
        return e;
    }

    private PayementVirement virementToDomain(PaiementVirementEntity e) {
        PayementVirement d = new PayementVirement();
        d.setBanque(e.getBanque());
        d.setNumeroCompte(e.getNumeroCompte());
        d.setReferenceVirement(e.getReferenceVirement());
        d.setDateValeur(e.getDateValeur());
        d.setCommission(e.getCommission());
        return d;
    }

    private PaiementEspecesEntity especesEntity(PayementEspeces d, UUID tenantId) {
        PaiementEspecesEntity e = new PaiementEspecesEntity();
        e.setCaja(d.getCaja());
        e.setNumeroRecepisse(d.getNumeroRecepisse());
        e.setAgent(d.getAgent());
        e.setContresigne(d.isContresigne());
        e.setTenantId(tenantId);
        return e;
    }

    private PayementEspeces especesToDomain(PaiementEspecesEntity e) {
        PayementEspeces d = new PayementEspeces();
        d.setCaja(e.getCaja());
        d.setNumeroRecepisse(e.getNumeroRecepisse());
        d.setAgent(e.getAgent());
        d.setContresigne(e.isContresigne());
        return d;
    }

    private RemboursementEntity remboursementEntity(Remboursement d, UUID tenantId) {
        RemboursementEntity e = new RemboursementEntity();
        e.setMontant(d.getMontant());
        e.setMotif(d.getMotif());
        e.setDate(d.getDate());
        e.setMoyen(d.getMoyen() == null ? null : d.getMoyen().name());
        e.setReferenceTransaction(d.getReferenceTransaction());
        e.setTenantId(tenantId);
        return e;
    }

    private Remboursement remboursementToDomain(RemboursementEntity e) {
        Remboursement d = new Remboursement();
        d.setId(e.getId());
        d.setPaiementId(e.getPaiementId());
        d.setMontant(e.getMontant());
        d.setMotif(e.getMotif());
        d.setDate(e.getDate());
        d.setMoyen(e.getMoyen() == null ? null : MoyenPaiement.valueOf(e.getMoyen()));
        d.setReferenceTransaction(e.getReferenceTransaction());
        return d;
    }

    // ---------- COUPON ----------

    @Override
    @Transactional
    public CouponPaiement saveCoupon(CouponPaiement coupon) {
        if (coupon.getId() == null) {
            coupon.setId(UUID.randomUUID());
        }
        couponRepository.save(couponEntity(coupon, tenant()));
        return findCoupon(coupon.getId()).orElse(coupon);
    }

    @Override
    public Optional<CouponPaiement> findCoupon(UUID id) {
        return couponRepository.findById(id).map(this::couponToDomain);
    }

    @Override
    public List<CouponPaiement> listCoupons(UUID eleveId, UUID anneeId) {
        return couponRepository.findAll().stream()
                .map(this::couponToDomain)
                .filter(c -> eleveId == null || eleveId.equals(c.getEleve()))
                .filter(c -> anneeId == null || anneeId.equals(c.getAnnee()))
                .toList();
    }

    @Override
    public long countCouponsAnnee(UUID anneeId) {
        return anneeId == null ? couponRepository.count() : couponRepository.countByAnnee(anneeId);
    }

    @Override
    public Optional<CouponPaiement> findCouponNumero(String numero) {
        return couponRepository.findByNumero(numero).map(this::couponToDomain);
    }

    private CouponPaiementEntity couponEntity(CouponPaiement d, UUID tenantId) {
        CouponPaiementEntity e = new CouponPaiementEntity();
        e.setId(d.getId());
        e.setNumero(d.getNumero());
        e.setSequence(d.getSequence());
        e.setAnnee(d.getAnnee());
        e.setEleve(d.getEleve());
        e.setPaiement(d.getPaiement());
        e.setAvis(d.getAvis());
        e.setLigneFrais(d.getLigneFrais());
        e.setDateEdition(d.getDateEdition());
        e.setMontant(d.getMontant());
        e.setLibelle(d.getLibelle());
        e.setMoyen(d.getMoyen() == null ? null : d.getMoyen().name());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setDevise(d.getDevise());
        e.setMention(d.getMention());
        e.setUrlQr(d.getUrlQr());
        e.setCodeBarre(d.getCodeBarre());
        e.setEmpreinte(d.getEmpreinte());
        e.setRemisLe(d.getRemisLe());
        e.setRemisA(d.getRemisA());
        e.setReferenceSignature(d.getReferenceSignature());
        e.setAnnule(d.isAnnule());
        e.setMotifAnnulation(d.getMotifAnnulation());
        e.setTenantId(tenantId);
        return e;
    }

    private CouponPaiement couponToDomain(CouponPaiementEntity e) {
        CouponPaiement d = new CouponPaiement();
        d.setId(e.getId());
        d.setNumero(e.getNumero());
        d.setSequence(e.getSequence());
        d.setAnnee(e.getAnnee());
        d.setEleve(e.getEleve());
        d.setPaiement(e.getPaiement());
        d.setAvis(e.getAvis());
        d.setLigneFrais(e.getLigneFrais());
        d.setDateEdition(e.getDateEdition());
        d.setMontant(e.getMontant());
        d.setLibelle(e.getLibelle());
        d.setMoyen(e.getMoyen() == null ? null : MoyenPaiement.valueOf(e.getMoyen()));
        d.setStatut(e.getStatut() == null ? null : StatutCoupon.valueOf(e.getStatut()));
        d.setDevise(e.getDevise());
        d.setMention(e.getMention());
        d.setUrlQr(e.getUrlQr());
        d.setCodeBarre(e.getCodeBarre());
        d.setEmpreinte(e.getEmpreinte());
        d.setRemisLe(e.getRemisLe());
        d.setRemisA(e.getRemisA());
        d.setReferenceSignature(e.getReferenceSignature());
        d.setAnnule(e.isAnnule());
        d.setMotifAnnulation(e.getMotifAnnulation());
        return d;
    }

    // ---------- JOURNALIER ----------

    @Override
    @Transactional
    public Journalier saveJournalier(Journalier journalier) {
        if (journalier.getId() == null) {
            journalier.setId(UUID.randomUUID());
        }
        journalierRepository.save(journalierEntity(journalier, tenant()));
        return findJournalier(journalier.getId()).orElse(journalier);
    }

    @Override
    public Optional<Journalier> findJournalier(UUID id) {
        return journalierRepository.findById(id).map(this::journalierToDomain);
    }

    @Override
    public List<Journalier> listJournaliers(LocalDate date, UUID site) {
        List<JournalierEntity> entities;
        if (date != null && site != null) {
            entities = journalierRepository.findByDate(date).stream()
                    .filter(j -> site.equals(j.getSite())).toList();
        } else if (date != null) {
            entities = journalierRepository.findByDate(date);
        } else if (site != null) {
            entities = journalierRepository.findBySite(site);
        } else {
            entities = journalierRepository.findAll();
        }
        return entities.stream().map(this::journalierToDomain).toList();
    }

    private JournalierEntity journalierEntity(Journalier d, UUID tenantId) {
        JournalierEntity e = new JournalierEntity();
        e.setId(d.getId());
        e.setDate(d.getDate());
        e.setSite(d.getSite());
        e.setStatut(d.getStatut());
        e.setFond(d.getFond());
        e.setTenantId(tenantId);
        return e;
    }

    private Journalier journalierToDomain(JournalierEntity e) {
        Journalier d = new Journalier();
        d.setId(e.getId());
        d.setDate(e.getDate());
        d.setSite(e.getSite());
        d.setStatut(e.getStatut());
        d.setFond(e.getFond());
        return d;
    }
}