package com.nexusgesti.structure.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.structure.adapter.out.persistence.entity.*;
import com.nexusgesti.structure.domain.model.*;
import com.nexusgesti.structure.domain.port.out.StructureRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StructurePersistenceAdapter implements StructureRepository {

    private final NiveauJpaRepository niveauJpaRepository;
    private final ClasseJpaRepository classeJpaRepository;
    private final PromotionJpaRepository promotionJpaRepository;
    private final SemestreJpaRepository semestreJpaRepository;
    private final UEJpaRepository ueJpaRepository;
    private final MatiereJpaRepository matiereJpaRepository;
    private final CoursECUEJpaRepository coursJpaRepository;
    private final SalleJpaRepository salleJpaRepository;
    private final CreneauJpaRepository creneauJpaRepository;
    private final RegimeHoraireJpaRepository regimeJpaRepository;
    private final EmploiDuTempsJpaRepository emploiJpaRepository;
    private final PlageEmploiJpaRepository plageJpaRepository;
    private final ConflitEmploiJpaRepository conflitJpaRepository;
    private final ObjectMapper objectMapper;

    public StructurePersistenceAdapter(NiveauJpaRepository niveauJpaRepository,
                                       ClasseJpaRepository classeJpaRepository,
                                       PromotionJpaRepository promotionJpaRepository,
                                       SemestreJpaRepository semestreJpaRepository,
                                       UEJpaRepository ueJpaRepository,
                                       MatiereJpaRepository matiereJpaRepository,
                                       CoursECUEJpaRepository coursJpaRepository,
                                       SalleJpaRepository salleJpaRepository,
                                       CreneauJpaRepository creneauJpaRepository,
                                       RegimeHoraireJpaRepository regimeJpaRepository,
                                       EmploiDuTempsJpaRepository emploiJpaRepository,
                                       PlageEmploiJpaRepository plageJpaRepository,
                                       ConflitEmploiJpaRepository conflitJpaRepository,
                                       ObjectMapper objectMapper) {
        this.niveauJpaRepository = niveauJpaRepository;
        this.classeJpaRepository = classeJpaRepository;
        this.promotionJpaRepository = promotionJpaRepository;
        this.semestreJpaRepository = semestreJpaRepository;
        this.ueJpaRepository = ueJpaRepository;
        this.matiereJpaRepository = matiereJpaRepository;
        this.coursJpaRepository = coursJpaRepository;
        this.salleJpaRepository = salleJpaRepository;
        this.creneauJpaRepository = creneauJpaRepository;
        this.regimeJpaRepository = regimeJpaRepository;
        this.emploiJpaRepository = emploiJpaRepository;
        this.plageJpaRepository = plageJpaRepository;
        this.conflitJpaRepository = conflitJpaRepository;
        this.objectMapper = objectMapper;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- NIVEAU ----------

    @Override
    public Niveau saveNiveau(Niveau niveau) {
        if (niveau.getIdNiveau() == null) {
            niveau.setIdNiveau(UUID.randomUUID());
        }
        NiveauEntity e = new NiveauEntity();
        e.setId(niveau.getIdNiveau());
        e.setCode(niveau.getCode());
        e.setLibelle(niveau.getLibelle());
        e.setRang(niveau.getRang());
        e.setCycle(niveau.getCycle() == null ? null : niveau.getCycle().name());
        e.setOrdre(niveau.getOrdre());
        e.setNiveauSuperieurId(niveau.getNiveauSuperieurId());
        e.setTenantId(tenant());
        niveauJpaRepository.save(e);
        return niveau;
    }

    @Override
    public Optional<Niveau> findNiveau(UUID id) {
        return niveauJpaRepository.findById(id).map(this::niveauToDomain);
    }

    @Override
    public List<Niveau> listNiveaux() {
        return niveauJpaRepository.findAll().stream().map(this::niveauToDomain).toList();
    }

    @Override
    public void deleteNiveau(UUID id) {
        niveauJpaRepository.deleteById(id);
    }

    // ---------- CLASSE ----------

    @Override
    public Classe saveClasse(Classe classe) {
        if (classe.getIdClasse() == null) {
            classe.setIdClasse(UUID.randomUUID());
        }
        ClasseEntity e = new ClasseEntity();
        e.setId(classe.getIdClasse());
        e.setCode(classe.getCode());
        e.setLibelle(classe.getLibelle());
        e.setNiveauId(classe.getNiveauId());
        e.setAnneeId(classe.getAnneeId());
        e.setSiteId(classe.getSiteId());
        e.setCapacite(classe.getCapacite());
        e.setEffectifReel(classe.getEffectifReel());
        e.setStatut(classe.getStatut() == null ? null : classe.getStatut().name());
        e.setSalleAttitreeId(classe.getSalleAttitreeId());
        e.setRegimeHoraireCode(classe.getRegimeHoraireCode());
        e.setTenantId(tenant());
        classeJpaRepository.save(e);
        return classe;
    }

    @Override
    public Optional<Classe> findClasse(UUID id) {
        return classeJpaRepository.findById(id).map(this::classeToDomain);
    }

    @Override
    public List<Classe> listClasses() {
        return classeJpaRepository.findAll().stream().map(this::classeToDomain).toList();
    }

    // ---------- PROMOTION ----------

    @Override
    public Promotion savePromotion(Promotion promotion) {
        if (promotion.getIdPromotion() == null) {
            promotion.setIdPromotion(UUID.randomUUID());
        }
        PromotionEntity e = new PromotionEntity();
        e.setId(promotion.getIdPromotion());
        e.setCode(promotion.getCode());
        e.setLibelle(promotion.getLibelle());
        e.setProgrammeId(promotion.getProgrammeId());
        e.setSemestreId(promotion.getSemestreId());
        e.setAnneeId(promotion.getAnneeId());
        e.setCapacite(promotion.getCapacite());
        e.setEffectifReel(promotion.getEffectifReel());
        e.setStatut(promotion.getStatut() == null ? null : promotion.getStatut().name());
        e.setTenantId(tenant());
        promotionJpaRepository.save(e);
        return promotion;
    }

    @Override
    public Optional<Promotion> findPromotion(UUID id) {
        return promotionJpaRepository.findById(id).map(this::promotionToDomain);
    }

    @Override
    public List<Promotion> listPromotions() {
        return promotionJpaRepository.findAll().stream().map(this::promotionToDomain).toList();
    }

    // ---------- SEMESTRE ----------

    @Override
    public Semestre saveSemestre(Semestre semestre) {
        if (semestre.getIdSemestre() == null) {
            semestre.setIdSemestre(UUID.randomUUID());
        }
        SemestreEntity e = new SemestreEntity();
        e.setId(semestre.getIdSemestre());
        e.setCode(semestre.getCode());
        e.setLibelle(semestre.getLibelle());
        e.setRang(semestre.getRang());
        e.setPeriodeId(semestre.getPeriodeId());
        e.setTotalCreditsAttendu(semestre.getTotalCreditsAttendu());
        e.setTenantId(tenant());
        semestreJpaRepository.save(e);
        return semestre;
    }

    @Override
    public Optional<Semestre> findSemestre(UUID id) {
        return semestreJpaRepository.findById(id).map(this::semestreToDomain);
    }

    @Override
    public List<Semestre> listSemestres() {
        return semestreJpaRepository.findAll().stream().map(this::semestreToDomain).toList();
    }

    // ---------- UE ----------

    @Override
    public UE saveUE(UE ue) {
        if (ue.getIdUE() == null) {
            ue.setIdUE(UUID.randomUUID());
        }
        UEEntity e = new UEEntity();
        e.setId(ue.getIdUE());
        e.setCode(ue.getCode());
        e.setLibelle(ue.getLibelle());
        e.setSemestreId(ue.getSemestreId());
        e.setCredits(ue.getCredits());
        e.setCoefficient(ue.getCoefficient());
        e.setVolumeHoraire(ue.getVolumeHoraire());
        e.setObligatoire(ue.isObligatoire());
        e.setNiveauAcalcul(ue.getNiveauAcalcul());
        e.setTenantId(tenant());
        ueJpaRepository.save(e);
        return ue;
    }

    @Override
    public Optional<UE> findUE(UUID id) {
        return ueJpaRepository.findById(id).map(this::ueToDomain);
    }

    @Override
    public List<UE> listUEs() {
        return ueJpaRepository.findAll().stream().map(this::ueToDomain).toList();
    }

    // ---------- MATIERE ----------

    @Override
    public Matiere saveMatiere(Matiere matiere) {
        if (matiere.getIdMatiere() == null) {
            matiere.setIdMatiere(UUID.randomUUID());
        }
        MatiereEntity e = new MatiereEntity();
        e.setId(matiere.getIdMatiere());
        e.setCode(matiere.getCode());
        e.setLibelle(matiere.getLibelle());
        e.setDiscipline(matiere.getDiscipline());
        e.setType(matiere.getType() == null ? null : matiere.getType().name());
        e.setCouleur(matiere.getCouleur());
        e.setActive(matiere.isActive());
        e.setTenantId(tenant());
        matiereJpaRepository.save(e);
        return matiere;
    }

    @Override
    public Optional<Matiere> findMatiere(UUID id) {
        return matiereJpaRepository.findById(id).map(this::matiereToDomain);
    }

    @Override
    public List<Matiere> listMatieres() {
        return matiereJpaRepository.findAll().stream().map(this::matiereToDomain).toList();
    }

    @Override
    public void deleteMatiere(UUID id) {
        matiereJpaRepository.deleteById(id);
    }

    // ---------- COURS ECUE ----------

    @Override
    public CoursECUE saveCours(CoursECUE cours) {
        if (cours.getIdCours() == null) {
            cours.setIdCours(UUID.randomUUID());
        }
        CoursECUEEntity e = new CoursECUEEntity();
        e.setId(cours.getIdCours());
        e.setCode(cours.getCode());
        e.setLibelle(cours.getLibelle());
        e.setUeId(cours.getUeId());
        e.setClasseId(cours.getClasseId());
        e.setMatiereId(cours.getMatiereId());
        e.setType(cours.getType() == null ? null : cours.getType().name());
        e.setVolumeHoraire(cours.getVolumeHoraire());
        e.setCredits(cours.getCredits());
        e.setCoefficient(cours.getCoefficient());
        e.setHebdomadaire(cours.isHebdomadaire());
        e.setRegroupement(cours.getRegroupement());
        e.setTenantId(tenant());
        coursJpaRepository.save(e);
        return cours;
    }

    @Override
    public Optional<CoursECUE> findCours(UUID id) {
        return coursJpaRepository.findById(id).map(this::coursToDomain);
    }

    @Override
    public List<CoursECUE> listCours() {
        return coursJpaRepository.findAll().stream().map(this::coursToDomain).toList();
    }

    // ---------- SALLE ----------

    @Override
    public Salle saveSalle(Salle salle) {
        if (salle.getIdSalle() == null) {
            salle.setIdSalle(UUID.randomUUID());
        }
        SalleEntity e = new SalleEntity();
        e.setId(salle.getIdSalle());
        e.setCode(salle.getCode());
        e.setLibelle(salle.getLibelle());
        e.setType(salle.getType() == null ? null : salle.getType().name());
        e.setSiteId(salle.getSiteId());
        e.setCapacite(salle.getCapacite());
        e.setEquipements(json(salle.getEquipements()));
        e.setAccessible(salle.isAccessible());
        e.setDisponible(salle.isDisponible());
        e.setTenantId(tenant());
        salleJpaRepository.save(e);
        return salle;
    }

    @Override
    public Optional<Salle> findSalle(UUID id) {
        return salleJpaRepository.findById(id).map(this::salleToDomain);
    }

    @Override
    public List<Salle> listSalles() {
        return salleJpaRepository.findAll().stream().map(this::salleToDomain).toList();
    }

    // ---------- CRENEAU ----------

    @Override
    public Creneau saveCreneau(Creneau creneau) {
        if (creneau.getIdCreneau() == null) {
            creneau.setIdCreneau(UUID.randomUUID());
        }
        CreneauEntity e = new CreneauEntity();
        e.setId(creneau.getIdCreneau());
        e.setJour(creneau.getJour() == null ? null : creneau.getJour().name());
        e.setHeureDebut(creneau.getHeureDebut());
        e.setHeureFin(creneau.getHeureFin());
        e.setOrdre(creneau.getOrdre());
        e.setTenantId(tenant());
        creneauJpaRepository.save(e);
        return creneau;
    }

    @Override
    public Optional<Creneau> findCreneau(UUID id) {
        return creneauJpaRepository.findById(id).map(this::creneauToDomain);
    }

    @Override
    public List<Creneau> listCreneaux() {
        return creneauJpaRepository.findAll().stream().map(this::creneauToDomain).toList();
    }

    // ---------- REGIME HORAIRE ----------

    @Override
    public RegimeHoraire saveRegimeHoraire(RegimeHoraire regime) {
        RegimeHoraireEntity e = regimeJpaRepository.findByCode(regime.getCode())
                .orElseGet(RegimeHoraireEntity::new);
        e.setId(e.getId() == null ? UUID.randomUUID() : e.getId());
        e.setCode(regime.getCode());
        e.setLibelle(regime.getLibelle());
        e.setJours(json(regime.getJours().stream().map(Enum::name).toList()));
        e.setCreneauxParJour(regime.getCreneauxParJour());
        e.setDureeCreneau(regime.getDureeCreneau());
        e.setPauseMatinale(regime.getPauseMatinale());
        e.setPauseMeridienne(regime.getPauseMeridienne());
        e.setTenantId(tenant());
        regimeJpaRepository.save(e);
        return regime;
    }

    @Override
    public Optional<RegimeHoraire> findRegimeHoraire(String code) {
        return regimeJpaRepository.findByCode(code).map(this::regimeToDomain);
    }

    @Override
    public List<RegimeHoraire> listRegimesHoraires() {
        return regimeJpaRepository.findAll().stream().map(this::regimeToDomain).toList();
    }

    // ---------- EMPLOI DU TEMPS ----------

    @Override
    @Transactional
    public EmploiDuTemps saveEmploi(EmploiDuTemps emploi) {
        if (emploi.getIdEmploi() == null) {
            emploi.setIdEmploi(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        plageJpaRepository.deleteByEmploiId(emploi.getIdEmploi());
        conflitJpaRepository.deleteByEmploiId(emploi.getIdEmploi());

        EmploiDuTempsEntity e = new EmploiDuTempsEntity();
        e.setId(emploi.getIdEmploi());
        e.setAnneeId(emploi.getAnneeId());
        e.setClasseId(emploi.getClasseId());
        e.setVersion(emploi.getVersion());
        e.setDateActivation(emploi.getDateActivation());
        e.setActive(emploi.isActive());
        e.setPublie(emploi.isPublie());
        e.setTenantId(tenantId);
        emploiJpaRepository.save(e);

        emploi.getPlages().forEach(p -> {
            if (p.getIdPlage() == null) {
                p.setIdPlage(UUID.randomUUID());
            }
            PlageEmploiEntity pe = new PlageEmploiEntity();
            pe.setId(p.getIdPlage());
            pe.setEmploiId(emploi.getIdEmploi());
            pe.setCoursId(p.getCoursId());
            pe.setCreneauId(p.getCreneauId());
            pe.setSalleId(p.getSalleId());
            pe.setEnseignantId(p.getEnseignantId());
            pe.setTenantId(tenantId);
            plageJpaRepository.save(pe);
        });

        emploi.getConflits().forEach(c -> {
            if (c.getIdConflit() == null) {
                c.setIdConflit(UUID.randomUUID());
            }
            ConflitEmploiEntity ce = new ConflitEmploiEntity();
            ce.setId(c.getIdConflit());
            ce.setEmploiId(emploi.getIdEmploi());
            ce.setType(c.getType() == null ? null : c.getType().name());
            ce.setPlageAId(c.getPlageAId());
            ce.setPlageBId(c.getPlageBId());
            ce.setTenantId(tenantId);
            conflitJpaRepository.save(ce);
        });

        return findEmploi(emploi.getIdEmploi()).orElseThrow();
    }

    @Override
    public Optional<EmploiDuTemps> findEmploi(UUID id) {
        return emploiJpaRepository.findById(id).map(this::emploiToDomain);
    }

    @Override
    public List<EmploiDuTemps> listEmplois() {
        return emploiJpaRepository.findAll().stream().map(this::emploiToDomain).toList();
    }

    // ---------- MAPPERS ----------

    private Niveau niveauToDomain(NiveauEntity e) {
        Niveau n = new Niveau();
        n.setIdNiveau(e.getId());
        n.setCode(e.getCode());
        n.setLibelle(e.getLibelle());
        n.setRang(e.getRang());
        n.setCycle(e.getCycle() == null ? null : TypeCycle.valueOf(e.getCycle()));
        n.setOrdre(e.getOrdre());
        n.setNiveauSuperieurId(e.getNiveauSuperieurId());
        return n;
    }

    private Classe classeToDomain(ClasseEntity e) {
        Classe c = new Classe();
        c.setIdClasse(e.getId());
        c.setCode(e.getCode());
        c.setLibelle(e.getLibelle());
        c.setNiveauId(e.getNiveauId());
        c.setAnneeId(e.getAnneeId());
        c.setSiteId(e.getSiteId());
        c.setCapacite(e.getCapacite());
        c.setEffectifReel(e.getEffectifReel());
        c.setStatut(e.getStatut() == null ? null : StatutClasse.valueOf(e.getStatut()));
        c.setSalleAttitreeId(e.getSalleAttitreeId());
        c.setRegimeHoraireCode(e.getRegimeHoraireCode());
        return c;
    }

    private Promotion promotionToDomain(PromotionEntity e) {
        Promotion p = new Promotion();
        p.setIdPromotion(e.getId());
        p.setCode(e.getCode());
        p.setLibelle(e.getLibelle());
        p.setProgrammeId(e.getProgrammeId());
        p.setSemestreId(e.getSemestreId());
        p.setAnneeId(e.getAnneeId());
        p.setCapacite(e.getCapacite());
        p.setEffectifReel(e.getEffectifReel());
        p.setStatut(e.getStatut() == null ? null : StatutClasse.valueOf(e.getStatut()));
        return p;
    }

    private Semestre semestreToDomain(SemestreEntity e) {
        Semestre s = new Semestre();
        s.setIdSemestre(e.getId());
        s.setCode(e.getCode());
        s.setLibelle(e.getLibelle());
        s.setRang(e.getRang());
        s.setPeriodeId(e.getPeriodeId());
        s.setTotalCreditsAttendu(e.getTotalCreditsAttendu());
        return s;
    }

    private UE ueToDomain(UEEntity e) {
        UE u = new UE();
        u.setIdUE(e.getId());
        u.setCode(e.getCode());
        u.setLibelle(e.getLibelle());
        u.setSemestreId(e.getSemestreId());
        u.setCredits(e.getCredits());
        u.setCoefficient(e.getCoefficient());
        u.setVolumeHoraire(e.getVolumeHoraire());
        u.setObligatoire(e.isObligatoire());
        u.setNiveauAcalcul(e.getNiveauAcalcul());
        return u;
    }

    private Matiere matiereToDomain(MatiereEntity e) {
        Matiere m = new Matiere();
        m.setIdMatiere(e.getId());
        m.setCode(e.getCode());
        m.setLibelle(e.getLibelle());
        m.setDiscipline(e.getDiscipline());
        m.setType(e.getType() == null ? null : TypeMatiere.valueOf(e.getType()));
        m.setCouleur(e.getCouleur());
        m.setActive(e.isActive());
        return m;
    }

    private CoursECUE coursToDomain(CoursECUEEntity e) {
        CoursECUE c = new CoursECUE();
        c.setIdCours(e.getId());
        c.setCode(e.getCode());
        c.setLibelle(e.getLibelle());
        c.setUeId(e.getUeId());
        c.setClasseId(e.getClasseId());
        c.setMatiereId(e.getMatiereId());
        c.setType(e.getType() == null ? null : TypeEvaluation.valueOf(e.getType()));
        c.setVolumeHoraire(e.getVolumeHoraire());
        c.setCredits(e.getCredits());
        c.setCoefficient(e.getCoefficient());
        c.setHebdomadaire(e.isHebdomadaire());
        c.setRegroupement(e.getRegroupement());
        return c;
    }

    private Salle salleToDomain(SalleEntity e) {
        Salle s = new Salle();
        s.setIdSalle(e.getId());
        s.setCode(e.getCode());
        s.setLibelle(e.getLibelle());
        s.setType(e.getType() == null ? null : TypeSalle.valueOf(e.getType()));
        s.setSiteId(e.getSiteId());
        s.setCapacite(e.getCapacite());
        s.setEquipements(parseJson(e.getEquipements()));
        s.setAccessible(e.isAccessible());
        s.setDisponible(e.isDisponible());
        return s;
    }

    private Creneau creneauToDomain(CreneauEntity e) {
        Creneau c = new Creneau();
        c.setIdCreneau(e.getId());
        c.setJour(e.getJour() == null ? null : JourSemaine.valueOf(e.getJour()));
        c.setHeureDebut(e.getHeureDebut());
        c.setHeureFin(e.getHeureFin());
        c.setOrdre(e.getOrdre());
        return c;
    }

    private RegimeHoraire regimeToDomain(RegimeHoraireEntity e) {
        RegimeHoraire r = new RegimeHoraire();
        r.setCode(e.getCode());
        r.setLibelle(e.getLibelle());
        r.setJours(parseJson(e.getJours()).stream().map(JourSemaine::valueOf).toList());
        r.setCreneauxParJour(e.getCreneauxParJour());
        r.setDureeCreneau(e.getDureeCreneau());
        r.setPauseMatinale(e.getPauseMatinale());
        r.setPauseMeridienne(e.getPauseMeridienne());
        return r;
    }

    private EmploiDuTemps emploiToDomain(EmploiDuTempsEntity e) {
        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.setIdEmploi(e.getId());
        emploi.setAnneeId(e.getAnneeId());
        emploi.setClasseId(e.getClasseId());
        emploi.setVersion(e.getVersion());
        emploi.setDateActivation(e.getDateActivation());
        emploi.setActive(e.isActive());
        emploi.setPublie(e.isPublie());
        emploi.setPlages(plageJpaRepository.findByEmploiId(e.getId()).stream().map(this::plageToDomain).toList());
        emploi.setConflits(conflitJpaRepository.findByEmploiId(e.getId()).stream().map(this::conflitToDomain).toList());
        return emploi;
    }

    private PlageEmploi plageToDomain(PlageEmploiEntity e) {
        PlageEmploi p = new PlageEmploi();
        p.setIdPlage(e.getId());
        p.setEmploiId(e.getEmploiId());
        p.setCoursId(e.getCoursId());
        p.setCreneauId(e.getCreneauId());
        p.setSalleId(e.getSalleId());
        p.setEnseignantId(e.getEnseignantId());
        return p;
    }

    private ConflitEmploi conflitToDomain(ConflitEmploiEntity e) {
        ConflitEmploi c = new ConflitEmploi();
        c.setIdConflit(e.getId());
        c.setEmploiId(e.getEmploiId());
        c.setType(e.getType() == null ? null : TypeConflit.valueOf(e.getType()));
        c.setPlageAId(e.getPlageAId());
        c.setPlageBId(e.getPlageBId());
        return c;
    }

    private String json(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? new ArrayList<>() : values);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private List<String> parseJson(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
