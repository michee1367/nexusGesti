package com.nexusgesti.tenant.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.adapter.out.persistence.entity.*;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.port.out.StructureRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StructurePersistenceAdapter implements StructureRepository {

    private final SiteJpaRepository siteJpaRepository;
    private final FiliereJpaRepository filiereJpaRepository;
    private final ProgrammeJpaRepository programmeJpaRepository;
    private final CycleJpaRepository cycleJpaRepository;
    private final ParametreJpaRepository parametreJpaRepository;
    private final GrillePresetJpaRepository grillePresetJpaRepository;
    private final ObjectMapper objectMapper;

    public StructurePersistenceAdapter(SiteJpaRepository siteJpaRepository,
                                       FiliereJpaRepository filiereJpaRepository,
                                       ProgrammeJpaRepository programmeJpaRepository,
                                       CycleJpaRepository cycleJpaRepository,
                                       ParametreJpaRepository parametreJpaRepository,
                                       GrillePresetJpaRepository grillePresetJpaRepository,
                                       ObjectMapper objectMapper) {
        this.siteJpaRepository = siteJpaRepository;
        this.filiereJpaRepository = filiereJpaRepository;
        this.programmeJpaRepository = programmeJpaRepository;
        this.cycleJpaRepository = cycleJpaRepository;
        this.parametreJpaRepository = parametreJpaRepository;
        this.grillePresetJpaRepository = grillePresetJpaRepository;
        this.objectMapper = objectMapper;
    }

    // ---------- SITE ----------

    @Override
    public Site saveSite(Site site) {
        SiteEntity entity = siteToEntity(site);
        entity.setTenantId(TenantContext.require().asUuid());
        return siteToDomain(siteJpaRepository.save(entity));
    }

    @Override
    public List<Site> findSites(UUID etablissementId) {
        return siteJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::siteToDomain).toList();
    }

    @Override
    public Optional<Site> findSite(UUID id, UUID etablissementId) {
        return siteJpaRepository.findByIdAndEtablissementId(id, etablissementId).map(this::siteToDomain);
    }

    @Override
    public void deleteSite(UUID id, UUID etablissementId) {
        siteJpaRepository.deleteByIdAndEtablissementId(id, etablissementId);
    }

    private SiteEntity siteToEntity(Site site) {
        SiteEntity e = new SiteEntity();
        e.setId(site.getIdSite());
        e.setEtablissementId(site.getEtablissementId());
        e.setCode(site.getCode());
        e.setLibelle(site.getLibelle());
        e.setPrincipal(site.isPrincipal());
        adresseToEntity(e, site.getAdresse());
        return e;
    }

    private Site siteToDomain(SiteEntity e) {
        Site site = new Site();
        site.setIdSite(e.getId());
        site.setEtablissementId(e.getEtablissementId());
        site.setCode(e.getCode());
        site.setLibelle(e.getLibelle());
        site.setPrincipal(e.isPrincipal());
        site.setAdresse(adresseFromEntity(e, e));
        return site;
    }

    private void adresseToEntity(SiteEntity e, Adresse a) {
        if (a == null) {
            return;
        }
        e.setAdrRue(a.getRue());
        e.setAdrAvenue(a.getAvenue());
        e.setAdrQuartier(a.getQuartier());
        e.setAdrCommune(a.getCommune());
        e.setAdrVille(a.getVille());
        e.setAdrProvince(a.getProvince());
        e.setAdrTerritoire(a.getTerritoire());
        e.setAdrCodePostal(a.getCodePostal());
        e.setAdrPays(a.getPays());
    }

    private Adresse adresseFromEntity(SiteEntity e, SiteEntity ignored) {
        Adresse a = new Adresse();
        a.setRue(e.getAdrRue());
        a.setAvenue(e.getAdrAvenue());
        a.setQuartier(e.getAdrQuartier());
        a.setCommune(e.getAdrCommune());
        a.setVille(e.getAdrVille());
        a.setProvince(e.getAdrProvince());
        a.setTerritoire(e.getAdrTerritoire());
        a.setCodePostal(e.getAdrCodePostal());
        a.setPays(e.getAdrPays());
        return a;
    }

    // ---------- FILIERE ----------

    @Override
    public Filiere saveFiliere(Filiere filiere) {
        FiliereEntity entity = filiereToEntity(filiere);
        entity.setTenantId(TenantContext.require().asUuid());
        return filiereToDomain(filiereJpaRepository.save(entity));
    }

    @Override
    public List<Filiere> findFilieres(UUID etablissementId) {
        return filiereJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::filiereToDomain).toList();
    }

    @Override
    public Optional<Filiere> findFiliere(UUID id, UUID etablissementId) {
        return filiereJpaRepository.findByIdAndEtablissementId(id, etablissementId).map(this::filiereToDomain);
    }

    @Override
    public void deleteFiliere(UUID id, UUID etablissementId) {
        filiereJpaRepository.deleteByIdAndEtablissementId(id, etablissementId);
    }

    private FiliereEntity filiereToEntity(Filiere f) {
        FiliereEntity e = new FiliereEntity();
        e.setId(f.getIdFiliere());
        e.setEtablissementId(f.getEtablissementId());
        e.setCode(f.getCode());
        e.setLibelle(f.getLibelle());
        e.setDepartement(f.getDepartement());
        e.setDiplome(f.getDiplome());
        e.setNiveauDiplome(f.getNiveauDiplome());
        e.setDureeAnnees(f.getDureeAnnees());
        e.setCycleId(f.getCycleId());
        e.setActive(f.isActive());
        return e;
    }

    private Filiere filiereToDomain(FiliereEntity e) {
        Filiere f = new Filiere();
        f.setIdFiliere(e.getId());
        f.setEtablissementId(e.getEtablissementId());
        f.setCode(e.getCode());
        f.setLibelle(e.getLibelle());
        f.setDepartement(e.getDepartement());
        f.setDiplome(e.getDiplome());
        f.setNiveauDiplome(e.getNiveauDiplome());
        f.setDureeAnnees(e.getDureeAnnees());
        f.setCycleId(e.getCycleId());
        f.setActive(e.isActive());
        return f;
    }

    // ---------- PROGRAMME ----------

    @Override
    public Programme saveProgramme(Programme programme) {
        ProgrammeEntity entity = programmeToEntity(programme);
        entity.setTenantId(TenantContext.require().asUuid());
        return programmeToDomain(programmeJpaRepository.save(entity));
    }

    @Override
    public List<Programme> findProgrammes(UUID etablissementId) {
        return filiereJpaRepository.findByEtablissementId(etablissementId).stream()
                .flatMap(f -> programmeJpaRepository.findByFiliereId(f.getId()).stream())
                .map(this::programmeToDomain).toList();
    }

    @Override
    public Optional<Programme> findProgramme(UUID id, UUID etablissementId) {
        Optional<FiliereEntity> filiere = filiereJpaRepository.findByEtablissementId(etablissementId).stream()
                .filter(f -> programmeJpaRepository.findByFiliereId(f.getId()).stream()
                        .anyMatch(p -> p.getId().equals(id)))
                .findFirst();
        return filiere.flatMap(f -> programmeJpaRepository.findByFiliereIdAndId(f.getId(), id))
                .map(this::programmeToDomain);
    }

    @Override
    public void deleteProgramme(UUID id, UUID etablissementId) {
        filiereJpaRepository.findByEtablissementId(etablissementId).stream()
                .filter(f -> programmeJpaRepository.findByFiliereId(f.getId()).stream()
                        .anyMatch(p -> p.getId().equals(id)))
                .findFirst()
                .ifPresent(f -> programmeJpaRepository.deleteByFiliereIdAndId(f.getId(), id));
    }

    private ProgrammeEntity programmeToEntity(Programme p) {
        ProgrammeEntity e = new ProgrammeEntity();
        e.setId(p.getIdProgramme());
        e.setFiliereId(p.getFiliereId());
        e.setCode(p.getCode());
        e.setLibelle(p.getLibelle());
        e.setParcours(json(p.getParcours()));
        e.setTotalCredits(p.getTotalCredits());
        e.setActive(p.isActive());
        return e;
    }

    private Programme programmeToDomain(ProgrammeEntity e) {
        Programme p = new Programme();
        p.setIdProgramme(e.getId());
        p.setFiliereId(e.getFiliereId());
        p.setCode(e.getCode());
        p.setLibelle(e.getLibelle());
        p.setParcours(parseList(e.getParcours()));
        p.setTotalCredits(e.getTotalCredits());
        p.setActive(e.isActive());
        return p;
    }

    // ---------- CYCLE ----------

    @Override
    public Cycle saveCycle(Cycle cycle) {
        CycleEntity entity = cycleToEntity(cycle);
        entity.setTenantId(TenantContext.require().asUuid());
        return cycleToDomain(cycleJpaRepository.save(entity));
    }

    @Override
    public List<Cycle> findCycles(UUID etablissementId) {
        return cycleJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::cycleToDomain).toList();
    }

    @Override
    public Optional<Cycle> findCycle(UUID id, UUID etablissementId) {
        return cycleJpaRepository.findByIdAndEtablissementId(id, etablissementId).map(this::cycleToDomain);
    }

    @Override
    public void deleteCycle(UUID id, UUID etablissementId) {
        cycleJpaRepository.deleteByIdAndEtablissementId(id, etablissementId);
    }

    private CycleEntity cycleToEntity(Cycle c) {
        CycleEntity e = new CycleEntity();
        e.setId(c.getId());
        e.setEtablissementId(c.getEtablissementId());
        e.setCode(c.getCode());
        e.setLibelle(c.getLibelle());
        e.setRang(c.getRang());
        e.setProfil(c.getProfil() == null ? null : c.getProfil().name());
        return e;
    }

    private Cycle cycleToDomain(CycleEntity e) {
        Cycle c = new Cycle();
        c.setId(e.getId());
        c.setEtablissementId(e.getEtablissementId());
        c.setCode(e.getCode());
        c.setLibelle(e.getLibelle());
        c.setRang(e.getRang());
        c.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        return c;
    }

    // ---------- PARAMETRE ----------

    @Override
    public Parametre saveParametre(Parametre parametre) {
        ParametreEntity entity = parametreToEntity(parametre);
        entity.setTenantId(TenantContext.require().asUuid());
        return parametreToDomain(parametreJpaRepository.save(entity));
    }

    @Override
    public List<Parametre> findParametres(UUID etablissementId) {
        return parametreJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::parametreToDomain).toList();
    }

    @Override
    public Optional<Parametre> findParametre(UUID id, UUID etablissementId) {
        return parametreJpaRepository.findByIdAndEtablissementId(id, etablissementId).map(this::parametreToDomain);
    }

    @Override
    public void deleteParametre(UUID id, UUID etablissementId) {
        parametreJpaRepository.deleteByIdAndEtablissementId(id, etablissementId);
    }

    private ParametreEntity parametreToEntity(Parametre p) {
        ParametreEntity e = new ParametreEntity();
        e.setId(p.getId());
        e.setEtablissementId(p.getEtablissementId());
        e.setCle(p.getCle());
        e.setValeur(p.getValeur());
        e.setTypeValeur(p.getTypeValeur() == null ? null : p.getTypeValeur().name());
        e.setCategorie(p.getCategorie());
        e.setDescription(p.getDescription());
        return e;
    }

    private Parametre parametreToDomain(ParametreEntity e) {
        Parametre p = new Parametre();
        p.setId(e.getId());
        p.setEtablissementId(e.getEtablissementId());
        p.setCle(e.getCle());
        p.setValeur(e.getValeur());
        p.setTypeValeur(e.getTypeValeur() == null ? null : TypeValeur.valueOf(e.getTypeValeur()));
        p.setCategorie(e.getCategorie());
        p.setDescription(e.getDescription());
        return p;
    }

    // ---------- GRILLE PRESET ----------

    @Override
    public GrillePreset savePreset(GrillePreset preset) {
        GrillePresetEntity entity = presetToEntity(preset);
        entity.setTenantId(TenantContext.require().asUuid());
        return presetToDomain(grillePresetJpaRepository.save(entity));
    }

    @Override
    public List<GrillePreset> findPresets(UUID etablissementId) {
        return grillePresetJpaRepository.findByEtablissementId(etablissementId).stream()
                .map(this::presetToDomain).toList();
    }

    @Override
    public Optional<GrillePreset> findPreset(UUID id, UUID etablissementId) {
        return grillePresetJpaRepository.findByIdAndEtablissementId(id, etablissementId).map(this::presetToDomain);
    }

    @Override
    public void deletePreset(UUID id, UUID etablissementId) {
        grillePresetJpaRepository.deleteByIdAndEtablissementId(id, etablissementId);
    }

    private GrillePresetEntity presetToEntity(GrillePreset p) {
        GrillePresetEntity e = new GrillePresetEntity();
        e.setId(p.getIdGrillePreset());
        e.setEtablissementId(p.getEtablissementId());
        e.setCode(p.getCode());
        e.setLibelle(p.getLibelle());
        e.setProfil(p.getProfil() == null ? null : p.getProfil().name());
        e.setDescription(p.getDescription());
        e.setSysteme(p.isSysteme());
        return e;
    }

    private GrillePreset presetToDomain(GrillePresetEntity e) {
        GrillePreset p = new GrillePreset();
        p.setIdGrillePreset(e.getId());
        p.setEtablissementId(e.getEtablissementId());
        p.setCode(e.getCode());
        p.setLibelle(e.getLibelle());
        p.setProfil(e.getProfil() == null ? null : ProfilEtablissement.valueOf(e.getProfil()));
        p.setDescription(e.getDescription());
        p.setSysteme(e.isSysteme());
        return p;
    }

    // ---------- HELPERS ----------

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
}