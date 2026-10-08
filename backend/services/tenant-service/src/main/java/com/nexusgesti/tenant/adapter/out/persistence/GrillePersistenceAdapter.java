package com.nexusgesti.tenant.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.adapter.out.persistence.entity.*;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.model.GrilleConformite;
import com.nexusgesti.tenant.domain.model.GrilleNotation;
import com.nexusgesti.tenant.domain.model.GrilleStock;
import com.nexusgesti.tenant.domain.port.out.GrilleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GrillePersistenceAdapter implements GrilleRepository {

    private final GrilleNotationJpaRepository notationJpaRepository;
    private final BaremeJpaRepository baremeJpaRepository;
    private final ReglePonderationJpaRepository reglePonderationJpaRepository;
    private final ConfigCascadeJpaRepository configCascadeJpaRepository;
    private final RegleEquivalenceJpaRepository regleEquivalenceJpaRepository;
    private final PrincipeEquivalenceJpaRepository principeEquivalenceJpaRepository;
    private final SeuilDecisionJpaRepository seuilDecisionJpaRepository;
    private final RegleAssiduiteJpaRepository regleAssiduiteJpaRepository;
    private final MentionJpaRepository mentionJpaRepository;
    private final RegleAppreciationJpaRepository regleAppreciationJpaRepository;
    private final GrilleStockJpaRepository stockJpaRepository;
    private final AlerteStockJpaRepository alerteStockJpaRepository;
    private final GrilleConformiteJpaRepository conformiteJpaRepository;
    private final ObjectMapper objectMapper;

    public GrillePersistenceAdapter(GrilleNotationJpaRepository notationJpaRepository,
                                    BaremeJpaRepository baremeJpaRepository,
                                    ReglePonderationJpaRepository reglePonderationJpaRepository,
                                    ConfigCascadeJpaRepository configCascadeJpaRepository,
                                    RegleEquivalenceJpaRepository regleEquivalenceJpaRepository,
                                    PrincipeEquivalenceJpaRepository principeEquivalenceJpaRepository,
                                    SeuilDecisionJpaRepository seuilDecisionJpaRepository,
                                    RegleAssiduiteJpaRepository regleAssiduiteJpaRepository,
                                    MentionJpaRepository mentionJpaRepository,
                                    RegleAppreciationJpaRepository regleAppreciationJpaRepository,
                                    GrilleStockJpaRepository stockJpaRepository,
                                    AlerteStockJpaRepository alerteStockJpaRepository,
                                    GrilleConformiteJpaRepository conformiteJpaRepository,
                                    ObjectMapper objectMapper) {
        this.notationJpaRepository = notationJpaRepository;
        this.baremeJpaRepository = baremeJpaRepository;
        this.reglePonderationJpaRepository = reglePonderationJpaRepository;
        this.configCascadeJpaRepository = configCascadeJpaRepository;
        this.regleEquivalenceJpaRepository = regleEquivalenceJpaRepository;
        this.principeEquivalenceJpaRepository = principeEquivalenceJpaRepository;
        this.seuilDecisionJpaRepository = seuilDecisionJpaRepository;
        this.regleAssiduiteJpaRepository = regleAssiduiteJpaRepository;
        this.mentionJpaRepository = mentionJpaRepository;
        this.regleAppreciationJpaRepository = regleAppreciationJpaRepository;
        this.stockJpaRepository = stockJpaRepository;
        this.alerteStockJpaRepository = alerteStockJpaRepository;
        this.conformiteJpaRepository = conformiteJpaRepository;
        this.objectMapper = objectMapper;
    }

    // ---------- GRILLE NOTATION ----------

    @Override
    @Transactional
    public GrilleNotation saveNotation(GrilleNotation grille) {
        UUID tenantId = TenantContext.require().asUuid();
        UUID grilleId = grille.getId();

        baremeJpaRepository.deleteByGrilleId(grilleId);
        reglePonderationJpaRepository.deleteByGrilleId(grilleId);
        configCascadeJpaRepository.deleteByGrilleId(grilleId);
        seuilDecisionJpaRepository.deleteByGrilleId(grilleId);
        regleAssiduiteJpaRepository.deleteByGrilleId(grilleId);
        mentionJpaRepository.deleteByGrilleId(grilleId);
        regleAppreciationJpaRepository.deleteByGrilleId(grilleId);

        notationJpaRepository.save(toNotationEntity(grille, tenantId));

        saveBarèmes(grille, tenantId);
        saveReglesPonderation(grille, tenantId);
        saveConfigCascade(grille, tenantId);
        saveSeuilsDecision(grille, tenantId);
        saveReglesAssiduite(grille, tenantId);
        saveMentions(grille, tenantId);
        saveAppreciations(grille, tenantId);

        return findNotation(grilleId).orElseThrow();
    }

    @Override
    public Optional<GrilleNotation> findNotation(UUID id) {
        return notationJpaRepository.findById(id).map(this::notationToDomain);
    }

    @Override
    public List<GrilleNotation> listNotations(UUID etablissementId) {
        return notationJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::notationToDomain).toList();
    }

    @Override
    public Optional<GrilleNotation> findNotationApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId) {
        Optional<GrilleNotationEntity> found = anneeId == null
                ? notationJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfil(etablissementId, profil.name())
                : notationJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
                        etablissementId, profil.name(), anneeId);
        return found.map(this::notationToDomain);
    }

    // ---------- GRILLE STOCK ----------

    @Override
    @Transactional
    public GrilleStock saveStock(GrilleStock grille) {
        UUID tenantId = TenantContext.require().asUuid();
        UUID grilleId = grille.getId();

        stockJpaRepository.deleteById(grilleId);
        alerteStockJpaRepository.deleteByGrilleId(grilleId);

        stockJpaRepository.save(toStockEntity(grille, tenantId));
        grille.getAlertes().forEach(a -> {
            AlerteStockEntity entity = alerteToEntity(a, grilleId, tenantId);
            alerteStockJpaRepository.save(entity);
        });

        return findStock(grilleId).orElseThrow();
    }

    @Override
    public Optional<GrilleStock> findStock(UUID id) {
        return stockJpaRepository.findById(id).map(this::stockToDomain);
    }

    @Override
    public List<GrilleStock> listStocks(UUID etablissementId) {
        return stockJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::stockToDomain).toList();
    }

    @Override
    public Optional<GrilleStock> findStockApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId) {
        Optional<GrilleStockEntity> found = anneeId == null
                ? stockJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfil(etablissementId, profil.name())
                : stockJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
                        etablissementId, profil.name(), anneeId);
        return found.map(this::stockToDomain);
    }

    @Override
    public AlerteStock saveAlerte(AlerteStock alerte) {
        UUID tenantId = TenantContext.require().asUuid();
        AlerteStockEntity entity = alerteToEntity(alerte, alerte.getGrilleId(), tenantId);
        return alerteToDomain(alerteStockJpaRepository.save(entity));
    }

    @Override
    public List<AlerteStock> findAlertes(UUID grilleId) {
        return alerteStockJpaRepository.findByGrilleId(grilleId).stream()
                .map(this::alerteToDomain).toList();
    }

    @Override
    public Optional<AlerteStock> findAlerte(UUID grilleId, UUID alerteId) {
        return alerteStockJpaRepository.findByIdAndGrilleId(alerteId, grilleId).map(this::alerteToDomain);
    }

    // ---------- GRILLE CONFORMITE ----------

    @Override
    @Transactional
    public GrilleConformite saveConformite(GrilleConformite grille) {
        UUID tenantId = TenantContext.require().asUuid();
        GrilleConformiteEntity entity = toConformiteEntity(grille, tenantId);
        conformiteJpaRepository.save(entity);
        return findConformite(grille.getId()).orElseThrow();
    }

    @Override
    public Optional<GrilleConformite> findConformite(UUID id) {
        return conformiteJpaRepository.findById(id).map(this::conformiteToDomain);
    }

    @Override
    public List<GrilleConformite> listConformites(UUID etablissementId) {
        return conformiteJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::conformiteToDomain).toList();
    }

    @Override
    public Optional<GrilleConformite> findConformiteApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId) {
        Optional<GrilleConformiteEntity> found = anneeId == null
                ? conformiteJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfil(etablissementId, profil.name())
                : conformiteJpaRepository.findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
                        etablissementId, profil.name(), anneeId);
        return found.map(this::conformiteToDomain);
    }

    // ---------- DESACTIVATION ----------

    @Override
    @Transactional
    public void desactiverAutresActives(UUID etablissementId, String typeGrille, String code, UUID aConserver) {
        switch (typeGrille) {
            case "GRANDE_NOTATION", "NOTATION" -> notationJpaRepository.desactiverAutres(etablissementId, code, aConserver);
            case "STOCK" -> stockJpaRepository.desactiverAutres(etablissementId, code, aConserver);
            case "CONFORMITE" -> conformiteJpaRepository.desactiverAutres(etablissementId, code, aConserver);
            default -> throw new IllegalArgumentException("Type de grille inconnu: " + typeGrille);
        }
    }

    // ---------- ENTITY MAPPERS ----------

    private GrilleNotationEntity toNotationEntity(GrilleNotation g, UUID tenantId) {
        GrilleNotationEntity e = new GrilleNotationEntity();
        fillGrille(e, g, tenantId);
        return e;
    }

    private void fillGrille(GrilleNotationEntity e, Grille g, UUID tenantId) {
        e.setId(g.getId());
        e.setEtablissementId(g.getEtablissementId());
        e.setCode(g.getCode());
        e.setLibelle(g.getLibelle());
        e.setVersion(g.getVersion());
        e.setProfil(g.getProfil() == null ? null : g.getProfil().name());
        e.setAnneeId(g.getAnneeId());
        e.setDateActivation(g.getDateActivation());
        e.setActive(g.isActive());
        e.setModifiable(g.isModifiable());
        e.setTenantId(tenantId);
    }

    private GrilleNotation notationToDomain(GrilleNotationEntity e) {
        GrilleNotation g = new GrilleNotation();
        g.setId(e.getId());
        g.setEtablissementId(e.getEtablissementId());
        g.setCode(e.getCode());
        g.setLibelle(e.getLibelle());
        g.setVersion(e.getVersion());
        g.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        g.setAnneeId(e.getAnneeId());
        g.setDateActivation(e.getDateActivation());
        g.setActive(e.isActive());
        g.setModifiable(e.isModifiable());
        g.setBaremes(baremeJpaRepository.findByGrilleId(e.getId()).stream().map(this::baremeToDomain).toList());
        g.setReglesPonderation(reglePonderationJpaRepository.findByGrilleId(e.getId()).stream()
                .map(this::reglePonderationToDomain).toList());
        configCascadeJpaRepository.findByGrilleId(e.getId()).ifPresent(cc -> {
            RegleEquivalence regleEquivalence = null;
            if (cc.getRegleEquivalenceId() != null) {
                RegleEquivalenceEntity re = regleEquivalenceJpaRepository.findById(cc.getRegleEquivalenceId()).orElse(null);
                if (re != null) {
                    regleEquivalence = new RegleEquivalence();
                    regleEquivalence.setStrategie(re.getStrategie());
                    regleEquivalence.setNombreMaxIgnorees(re.getNombreMaxIgnorees());
                    regleEquivalence.setMoyenneMinimaleIgnoree(re.getMoyenneMinimaleIgnoree());
                    regleEquivalence.setPrincipes(principeEquivalenceJpaRepository.findByRegleEquivalenceId(re.getId()).stream()
                            .map(this::principeToDomain).toList());
                }
            }
            ConfigCascade config = new ConfigCascade();
            config.setNiveauxActifs(parseNiveaux(cc.getNiveauxActifs()));
            config.setStrategie(cc.getStrategie() == null ? null : StrategieAggregation.valueOf(cc.getStrategie()));
            config.setArrondi(cc.getArrondi() == null ? null : TypeArrondi.valueOf(cc.getArrondi()));
            config.setPrecision(cc.getPrecision());
            config.setSeuilAbsorption(cc.getSeuilAbsorption());
            config.setRegleEquivalence(regleEquivalence);
            config.setOrdreConversionLettre(cc.isOrdreConversionLettre());
            g.setConfigCascade(config);
        });
        g.setSeuilsDecision(seuilDecisionJpaRepository.findByGrilleId(e.getId()).stream()
                .map(this::seuilDecisionToDomain).toList());
        g.setReglesAssiduite(regleAssiduiteJpaRepository.findByGrilleId(e.getId()).stream()
                .map(this::regleAssiduiteToDomain).toList());
        g.setMentions(mentionJpaRepository.findByGrilleId(e.getId()).stream().map(this::mentionToDomain).toList());
        g.setAppreciations(regleAppreciationJpaRepository.findByGrilleId(e.getId()).stream()
                .map(this::appreciationToDomain).toList());
        return g;
    }

    private GrilleStockEntity toStockEntity(GrilleStock g, UUID tenantId) {
        GrilleStockEntity e = new GrilleStockEntity();
        e.setId(g.getId());
        e.setEtablissementId(g.getEtablissementId());
        e.setCode(g.getCode());
        e.setLibelle(g.getLibelle());
        e.setVersion(g.getVersion());
        e.setProfil(g.getProfil() == null ? null : g.getProfil().name());
        e.setAnneeId(g.getAnneeId());
        e.setDateActivation(g.getDateActivation());
        e.setActive(g.isActive());
        e.setModifiable(g.isModifiable());
        e.setStrategieRotationLot(g.getStrategieRotationLot() == null ? null : g.getStrategieRotationLot().name());
        e.setDureeQuarantaineJours(g.getDureeQuarantaineJours());
        e.setSeuilAlertePeremptionJours(g.getSeuilAlertePeremptionJours());
        e.setToleranceEcartInventairePct(g.getToleranceEcartInventairePct());
        e.setDelaiReapproJours(g.getDelaiReapproJours());
        e.setSeuilAlerteTauxRotationPct(g.getSeuilAlerteTauxRotationPct());
        e.setAutoriserPeremptionEnSortie(g.isAutoriserPeremptionEnSortie());
        e.setExigerControleReception(g.isExigerControleReception());
        e.setTenantId(tenantId);
        return e;
    }

    private GrilleStock stockToDomain(GrilleStockEntity e) {
        GrilleStock g = new GrilleStock();
        g.setId(e.getId());
        g.setEtablissementId(e.getEtablissementId());
        g.setCode(e.getCode());
        g.setLibelle(e.getLibelle());
        g.setVersion(e.getVersion());
        g.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        g.setAnneeId(e.getAnneeId());
        g.setDateActivation(e.getDateActivation());
        g.setActive(e.isActive());
        g.setModifiable(e.isModifiable());
        g.setStrategieRotationLot(e.getStrategieRotationLot() == null ? null : StrategieRotationLot.valueOf(e.getStrategieRotationLot()));
        g.setDureeQuarantaineJours(e.getDureeQuarantaineJours());
        g.setSeuilAlertePeremptionJours(e.getSeuilAlertePeremptionJours());
        g.setToleranceEcartInventairePct(e.getToleranceEcartInventairePct());
        g.setDelaiReapproJours(e.getDelaiReapproJours());
        g.setSeuilAlerteTauxRotationPct(e.getSeuilAlerteTauxRotationPct());
        g.setAutoriserPeremptionEnSortie(e.isAutoriserPeremptionEnSortie());
        g.setExigerControleReception(e.isExigerControleReception());
        g.setAlertes(alerteStockJpaRepository.findByGrilleId(e.getId()).stream()
                .map(this::alerteToDomain).toList());
        return g;
    }

    private GrilleConformiteEntity toConformiteEntity(GrilleConformite g, UUID tenantId) {
        GrilleConformiteEntity e = new GrilleConformiteEntity();
        e.setId(g.getId());
        e.setEtablissementId(g.getEtablissementId());
        e.setCode(g.getCode());
        e.setLibelle(g.getLibelle());
        e.setVersion(g.getVersion());
        e.setProfil(g.getProfil() == null ? null : g.getProfil().name());
        e.setAnneeId(g.getAnneeId());
        e.setDateActivation(g.getDateActivation());
        e.setActive(g.isActive());
        e.setModifiable(g.isModifiable());
        e.setDureeRetentionDossierMedical(g.getDureeRetentionDossierMedical());
        e.setDureeConservationResultats(g.getDureeConservationResultats());
        e.setDureeConservationImages(g.getDureeConservationImages());
        e.setDureeConservationEchantillons(g.getDureeConservationEchantillons());
        e.setChiffrementAuRepos(g.isChiffrementAuRepos());
        e.setAccesTracable(g.isAccesTracable());
        e.setDelaiDeclarationResultatCritique(g.getDelaiDeclarationResultatCritique());
        e.setNiveauAccesResultatCritique(g.getNiveauAccesResultatCritique());
        e.setArchivable(g.isArchivable());
        e.setTenantId(tenantId);
        return e;
    }

    private GrilleConformite conformiteToDomain(GrilleConformiteEntity e) {
        GrilleConformite g = new GrilleConformite();
        g.setId(e.getId());
        g.setEtablissementId(e.getEtablissementId());
        g.setCode(e.getCode());
        g.setLibelle(e.getLibelle());
        g.setVersion(e.getVersion());
        g.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        g.setAnneeId(e.getAnneeId());
        g.setDateActivation(e.getDateActivation());
        g.setActive(e.isActive());
        g.setModifiable(e.isModifiable());
        g.setDureeRetentionDossierMedical(e.getDureeRetentionDossierMedical());
        g.setDureeConservationResultats(e.getDureeConservationResultats());
        g.setDureeConservationImages(e.getDureeConservationImages());
        g.setDureeConservationEchantillons(e.getDureeConservationEchantillons());
        g.setChiffrementAuRepos(e.isChiffrementAuRepos());
        g.setAccesTracable(e.isAccesTracable());
        g.setDelaiDeclarationResultatCritique(e.getDelaiDeclarationResultatCritique());
        g.setNiveauAccesResultatCritique(e.getNiveauAccesResultatCritique());
        g.setArchivable(e.isArchivable());
        return g;
    }

    // ---------- CHILD MAPPERS ----------

    private void saveBarèmes(GrilleNotation g, UUID tenantId) {
        g.getBaremes().forEach(b -> {
            BaremeEntity e = new BaremeEntity();
            e.setId(b.getIdBareme() == null ? UUID.randomUUID() : b.getIdBareme());
            e.setGrilleId(g.getId());
            e.setCode(b.getCode());
            e.setLibelle(b.getLibelle());
            e.setType(b.getType() == null ? null : b.getType().name());
            e.setBaremeMax(b.getBaremeMax());
            e.setNoteMinimale(b.getNoteMinimale());
            e.setPrecision(b.getPrecision());
            e.setArrondi(b.getArrondi() == null ? null : b.getArrondi().name());
            e.setDefaut(b.isDefaut());
            e.setTenantId(tenantId);
            baremeJpaRepository.save(e);
        });
    }

    private Bareme baremeToDomain(BaremeEntity e) {
        Bareme b = new Bareme();
        b.setIdBareme(e.getId());
        b.setGrilleId(e.getGrilleId());
        b.setCode(e.getCode());
        b.setLibelle(e.getLibelle());
        b.setType(e.getType() == null ? null : TypeBareme.valueOf(e.getType()));
        b.setBaremeMax(e.getBaremeMax());
        b.setNoteMinimale(e.getNoteMinimale());
        b.setPrecision(e.getPrecision());
        b.setArrondi(e.getArrondi() == null ? null : TypeArrondi.valueOf(e.getArrondi()));
        b.setDefaut(e.isDefaut());
        return b;
    }

    private void saveReglesPonderation(GrilleNotation g, UUID tenantId) {
        g.getReglesPonderation().forEach(r -> {
            ReglePonderationEntity e = toReglePonderationEntity(r, g.getId(), tenantId);
            reglePonderationJpaRepository.save(e);
        });
    }

    private ReglePonderationEntity toReglePonderationEntity(ReglePonderation r, UUID grilleId, UUID tenantId) {
        ReglePonderationEntity e = new ReglePonderationEntity();
        e.setId(r.getIdRegle() == null ? UUID.randomUUID() : r.getIdRegle());
        e.setGrilleId(grilleId);
        e.setLibelle(r.getLibelle());
        e.setPortee(r.getPortee() == null ? null : r.getPortee().name());
        e.setProfil(r.getProfil() == null ? null : r.getProfil().name());
        e.setCycleId(r.getCycleId());
        e.setNiveau(r.getNiveau());
        e.setParcours(r.getParcours());
        e.setProgramme(r.getProgramme());
        e.setSemestre(r.getSemestre());
        e.setUe(r.getUe());
        e.setCours(r.getCours());
        e.setMatiere(r.getMatiere());
        e.setEnseignantId(r.getEnseignant());
        e.setClasseId(r.getClasse());
        e.setTypeEvaluation(r.getTypeEvaluation());
        e.setTypeSession(r.getTypeSession());
        e.setCoefficient(r.getCoefficient());
        e.setSeuilInclusion(r.getSeuilInclusion());
        e.setPlafondContribution(r.getPlafondContribution());
        e.setPoidsMax(r.getPoidsMax());
        e.setPriorite(r.getPriorite());
        e.setActive(r.isActive());
        e.setTenantId(tenantId);
        return e;
    }

    private ReglePonderation reglePonderationToDomain(ReglePonderationEntity e) {
        ReglePonderation r = new ReglePonderation();
        r.setIdRegle(e.getId());
        r.setGrilleId(e.getGrilleId());
        r.setLibelle(e.getLibelle());
        r.setPortee(e.getPortee() == null ? null : TypePortee.valueOf(e.getPortee()));
        r.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        r.setCycleId(e.getCycleId());
        r.setNiveau(e.getNiveau());
        r.setParcours(e.getParcours());
        r.setProgramme(e.getProgramme());
        r.setSemestre(e.getSemestre());
        r.setUe(e.getUe());
        r.setCours(e.getCours());
        r.setMatiere(e.getMatiere());
        r.setEnseignant(e.getEnseignantId());
        r.setClasse(e.getClasseId());
        r.setTypeEvaluation(e.getTypeEvaluation());
        r.setTypeSession(e.getTypeSession());
        r.setCoefficient(e.getCoefficient());
        r.setSeuilInclusion(e.getSeuilInclusion());
        r.setPlafondContribution(e.getPlafondContribution());
        r.setPoidsMax(e.getPoidsMax());
        r.setPriorite(e.getPriorite());
        r.setActive(e.isActive());
        return r;
    }

    private void saveConfigCascade(GrilleNotation g, UUID tenantId) {
        ConfigCascade config = g.getConfigCascade();
        if (config == null) {
            return;
        }
        regleEquivalenceJpaRepository.findByGrilleId(g.getId()).forEach(ancienne ->
                principeEquivalenceJpaRepository.deleteByRegleEquivalenceId(ancienne.getId()));
        regleEquivalenceJpaRepository.deleteByGrilleId(g.getId());
        UUID regleEquivalenceId = null;
        if (config.getRegleEquivalence() != null) {
            RegleEquivalence re = config.getRegleEquivalence();
            final UUID regleId = UUID.randomUUID();
            regleEquivalenceId = regleId;
            RegleEquivalenceEntity reEntity = new RegleEquivalenceEntity();
            reEntity.setId(regleId);
            reEntity.setGrilleId(g.getId());
            reEntity.setStrategie(re.getStrategie());
            reEntity.setNombreMaxIgnorees(re.getNombreMaxIgnorees());
            reEntity.setMoyenneMinimaleIgnoree(re.getMoyenneMinimaleIgnoree());
            reEntity.setTenantId(tenantId);
            regleEquivalenceJpaRepository.save(reEntity);
            re.getPrincipes().forEach(p -> {
                PrincipeEquivalenceEntity pe = new PrincipeEquivalenceEntity();
                pe.setId(UUID.randomUUID());
                pe.setRegleEquivalenceId(regleId);
                pe.setType(p.getType() == null ? null : p.getType().name());
                pe.setCible(p.getCible());
                pe.setPonderation(p.getPonderation());
                pe.setTenantId(tenantId);
                principeEquivalenceJpaRepository.save(pe);
            });
        }
        ConfigCascadeEntity e = new ConfigCascadeEntity();
        e.setId(UUID.randomUUID());
        e.setGrilleId(g.getId());
        e.setNiveauxActifs(json(config.getNiveauxActifs()));
        e.setStrategie(config.getStrategie() == null ? null : config.getStrategie().name());
        e.setArrondi(config.getArrondi() == null ? null : config.getArrondi().name());
        e.setPrecision(config.getPrecision());
        e.setSeuilAbsorption(config.getSeuilAbsorption());
        e.setRegleEquivalenceId(regleEquivalenceId);
        e.setOrdreConversionLettre(config.isOrdreConversionLettre());
        e.setTenantId(tenantId);
        configCascadeJpaRepository.save(e);
    }

    private void saveSeuilsDecision(GrilleNotation g, UUID tenantId) {
        g.getSeuilsDecision().forEach(s -> {
            SeuilDecisionEntity e = new SeuilDecisionEntity();
            e.setId(s.getIdSeuil() == null ? UUID.randomUUID() : s.getIdSeuil());
            e.setGrilleId(g.getId());
            e.setLibelle(s.getLibelle());
            e.setPortee(s.getPortee() == null ? null : s.getPortee().name());
            e.setCycleId(s.getCycleId());
            e.setNiveau(s.getNiveau());
            e.setParcours(s.getParcours());
            e.setProgramme(s.getProgramme());
            e.setMoyenneMinimale(s.getMoyenneMinimale());
            e.setMoyenneMaximale(s.getMoyenneMaximale());
            e.setTauxReussiteMinimale(s.getTauxReussiteMinimale());
            e.setMoyenneDeReussite(s.getMoyenneDeReussite());
            e.setAbsencesMaximales(s.getAbsencesMaximales());
            e.setDecisionResultante(s.getDecisionResultante() == null ? null : s.getDecisionResultante().name());
            e.setRangMin(s.getRangMin());
            e.setRangMax(s.getRangMax());
            e.setPriorite(s.getPriorite());
            e.setTenantId(tenantId);
            seuilDecisionJpaRepository.save(e);
        });
    }

    private SeuilDecision seuilDecisionToDomain(SeuilDecisionEntity e) {
        SeuilDecision s = new SeuilDecision();
        s.setIdSeuil(e.getId());
        s.setGrilleId(e.getGrilleId());
        s.setLibelle(e.getLibelle());
        s.setPortee(e.getPortee() == null ? null : TypePortee.valueOf(e.getPortee()));
        s.setCycleId(e.getCycleId());
        s.setNiveau(e.getNiveau());
        s.setParcours(e.getParcours());
        s.setProgramme(e.getProgramme());
        s.setMoyenneMinimale(e.getMoyenneMinimale());
        s.setMoyenneMaximale(e.getMoyenneMaximale());
        s.setTauxReussiteMinimale(e.getTauxReussiteMinimale());
        s.setMoyenneDeReussite(e.getMoyenneDeReussite());
        s.setAbsencesMaximales(e.getAbsencesMaximales());
        s.setDecisionResultante(e.getDecisionResultante() == null ? null : Decision.valueOf(e.getDecisionResultante()));
        s.setRangMin(e.getRangMin());
        s.setRangMax(e.getRangMax());
        s.setPriorite(e.getPriorite());
        return s;
    }

    private void saveReglesAssiduite(GrilleNotation g, UUID tenantId) {
        g.getReglesAssiduite().forEach(r -> {
            RegleAssiduiteEntity e = new RegleAssiduiteEntity();
            e.setId(r.getIdRegle() == null ? UUID.randomUUID() : r.getIdRegle());
            e.setGrilleId(g.getId());
            e.setPortee(r.getPortee() == null ? null : r.getPortee().name());
            e.setCycleId(r.getCycleId());
            e.setNiveau(r.getNiveau());
            e.setAbsencesMaxParPeriode(r.getAbsencesMaxParPeriode());
            e.setAbsencesParJourMax(r.getAbsencesParJourMax());
            e.setTauxPresenceMinimale(r.getTauxPresenceMinimale());
            e.setAbsencesJustifieesExonerees(r.isAbsencesJustifieesExonerees());
            e.setRetardCompteCommeAbsence(r.isRetardCompteCommeAbsence());
            e.setSeuilRetardMinutes(r.getSeuilRetardMinutes());
            e.setConsequence(r.getConsequence() == null ? null : r.getConsequence().name());
            e.setPriorite(r.getPriorite());
            e.setTenantId(tenantId);
            regleAssiduiteJpaRepository.save(e);
        });
    }

    private RegleAssiduite regleAssiduiteToDomain(RegleAssiduiteEntity e) {
        RegleAssiduite r = new RegleAssiduite();
        r.setIdRegle(e.getId());
        r.setGrilleId(e.getGrilleId());
        r.setPortee(e.getPortee() == null ? null : TypePortee.valueOf(e.getPortee()));
        r.setCycleId(e.getCycleId());
        r.setNiveau(e.getNiveau());
        r.setAbsencesMaxParPeriode(e.getAbsencesMaxParPeriode());
        r.setAbsencesParJourMax(e.getAbsencesParJourMax());
        r.setTauxPresenceMinimale(e.getTauxPresenceMinimale());
        r.setAbsencesJustifieesExonerees(e.isAbsencesJustifieesExonerees());
        r.setRetardCompteCommeAbsence(e.isRetardCompteCommeAbsence());
        r.setSeuilRetardMinutes(e.getSeuilRetardMinutes());
        r.setConsequence(e.getConsequence() == null ? null : Decision.valueOf(e.getConsequence()));
        r.setPriorite(e.getPriorite());
        return r;
    }

    private void saveMentions(GrilleNotation g, UUID tenantId) {
        g.getMentions().forEach(m -> {
            MentionEntity e = new MentionEntity();
            e.setId(m.getIdMention() == null ? UUID.randomUUID() : m.getIdMention());
            e.setGrilleId(g.getId());
            e.setCode(m.getCode());
            e.setLibelle(m.getLibelle());
            e.setSymbole(m.getSymbole());
            e.setSeuilMinimum(m.getSeuilMinimum());
            e.setSeuilMaximum(m.getSeuilMaximum());
            e.setCouleur(m.getCouleur());
            e.setAppreciationAssociee(m.getAppreciationAssociee());
            e.setOrdre(m.getOrdre());
            e.setTenantId(tenantId);
            mentionJpaRepository.save(e);
        });
    }

    private Mention mentionToDomain(MentionEntity e) {
        Mention m = new Mention();
        m.setIdMention(e.getId());
        m.setGrilleId(e.getGrilleId());
        m.setCode(e.getCode());
        m.setLibelle(e.getLibelle());
        m.setSymbole(e.getSymbole());
        m.setSeuilMinimum(e.getSeuilMinimum());
        m.setSeuilMaximum(e.getSeuilMaximum());
        m.setCouleur(e.getCouleur());
        m.setAppreciationAssociee(e.getAppreciationAssociee());
        m.setOrdre(e.getOrdre());
        return m;
    }

    private void saveAppreciations(GrilleNotation g, UUID tenantId) {
        g.getAppreciations().forEach(a -> {
            RegleAppreciationEntity e = new RegleAppreciationEntity();
            e.setId(a.getIdRegle() == null ? UUID.randomUUID() : a.getIdRegle());
            e.setGrilleId(g.getId());
            e.setPortee(a.getPortee() == null ? null : a.getPortee().name());
            e.setMoyenneMinimale(a.getMoyenneMinimale());
            e.setMoyenneMaximale(a.getMoyenneMaximale());
            e.setLibelle(a.getLibelle());
            e.setModeleTexte(a.getModeleTexte());
            e.setMentionId(a.getMentionId());
            e.setTenantId(tenantId);
            regleAppreciationJpaRepository.save(e);
        });
    }

    private RegleAppreciation appreciationToDomain(RegleAppreciationEntity e) {
        RegleAppreciation a = new RegleAppreciation();
        a.setIdRegle(e.getId());
        a.setGrilleId(e.getGrilleId());
        a.setPortee(e.getPortee() == null ? null : TypePortee.valueOf(e.getPortee()));
        a.setMoyenneMinimale(e.getMoyenneMinimale());
        a.setMoyenneMaximale(e.getMoyenneMaximale());
        a.setLibelle(e.getLibelle());
        a.setModeleTexte(e.getModeleTexte());
        a.setMentionId(e.getMentionId());
        return a;
    }

    private AlerteStockEntity alerteToEntity(AlerteStock a, UUID grilleId, UUID tenantId) {
        AlerteStockEntity e = new AlerteStockEntity();
        e.setId(a.getIdAlerte() == null ? UUID.randomUUID() : a.getIdAlerte());
        e.setGrilleId(grilleId);
        e.setReference(a.getReference());
        e.setDepot(a.getDepot());
        e.setSeuil(a.getSeuil());
        e.setNiveau(a.getNiveau() == null ? null : a.getNiveau().name());
        e.setDestinataires(json(a.getDestinataires()));
        e.setActive(a.isActive());
        e.setTenantId(tenantId);
        return e;
    }

    private AlerteStock alerteToDomain(AlerteStockEntity e) {
        AlerteStock a = new AlerteStock();
        a.setIdAlerte(e.getId());
        a.setGrilleId(e.getGrilleId());
        a.setReference(e.getReference());
        a.setDepot(e.getDepot());
        a.setSeuil(e.getSeuil());
        a.setNiveau(e.getNiveau() == null ? null : NiveauAlerte.valueOf(e.getNiveau()));
        a.setDestinataires(parseUuids(e.getDestinataires()));
        a.setActive(e.isActive());
        return a;
    }

    private PrincipeEquivalence principeToDomain(PrincipeEquivalenceEntity e) {
        PrincipeEquivalence p = new PrincipeEquivalence();
        p.setType(e.getType() == null ? null : TypePrincipe.valueOf(e.getType()));
        p.setCible(e.getCible());
        p.setPonderation(e.getPonderation());
        return p;
    }

    private List<NiveauAcalcul> parseNiveaux(String json) {
        List<String> raw = parseList(json);
        return raw.stream().map(NiveauAcalcul::valueOf).toList();
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? new ArrayList<>() : value);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private List<UUID> parseUuids(String json) {
        List<String> raw = parseList(json);
        try {
            return raw.stream().map(UUID::fromString).toList();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}