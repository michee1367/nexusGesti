package com.nexusgesti.tenant.application.service;

import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import com.nexusgesti.tenant.domain.port.out.EtablissementRepository;
import com.nexusgesti.tenant.domain.port.out.ReferentielRepository;
import com.nexusgesti.tenant.domain.port.out.StructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EtablissementService implements EtablissementUseCase {

    private final EtablissementRepository etablissementRepository;
    private final StructureRepository structureRepository;
    private final ReferentielRepository referentielRepository;

    public EtablissementService(EtablissementRepository etablissementRepository,
                                StructureRepository structureRepository,
                                ReferentielRepository referentielRepository) {
        this.etablissementRepository = etablissementRepository;
        this.structureRepository = structureRepository;
        this.referentielRepository = referentielRepository;
    }

    @Override
    @Transactional
    public Etablissement creer(UUID id, Etablissement etablissement) {
        if (etablissementRepository.existsById(id)) {
            throw TenantException.tenantAlreadyExists(id);
        }
        etablissement.setId(id);
        if (etablissement.getStatut() == null) {
            etablissement.setStatut(StatutEtablissement.PREPARATION);
        }
        if (etablissement.getProfil() == null) {
            etablissement.setProfil(ProfilEtablissement.PRIMAIRE);
        }
        Etablissement cree = etablissementRepository.save(etablissement);
        creerReferentiels(id, etablissement.getProfil());
        return cree;
    }

    @Override
    public Etablissement get(UUID id) {
        return etablissementRepository.findById(id)
                .orElseThrow(() -> TenantException.notFound("Etablissement", id));
    }

    @Override
    @Transactional
    public Etablissement changerStatut(UUID id, StatutEtablissement cible, String motif) {
        Etablissement etablissement = get(id);
        switch (cible) {
            case ACTIF -> etablissement.activer();
            case SUSPENDU -> etablissement.suspendre(motif);
            case ARCHIVE -> etablissement.archiver();
            default -> throw TenantException.conflit("TRANSITION_INVALIDE",
                    "Statut cible non geré : " + cible);
        }
        return etablissementRepository.save(etablissement);
    }

    @Override
    public Etablissement getEtablissement(UUID etablissementId) {
        return get(etablissementId);
    }

    @Override
    public Site addSite(UUID etablissementId, Site site) {
        get(etablissementId);
        site.setIdSite(UUID.randomUUID());
        site.setEtablissementId(etablissementId);
        return structureRepository.saveSite(site);
    }

    @Override
    public List<Site> listSites(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findSites(etablissementId);
    }

    @Override
    public Site getSite(UUID etablissementId, UUID siteId) {
        get(etablissementId);
        return structureRepository.findSite(siteId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("Site", siteId));
    }

    @Override
    public Site updateSite(UUID etablissementId, UUID siteId, Site site) {
        getSite(etablissementId, siteId);
        site.setIdSite(siteId);
        site.setEtablissementId(etablissementId);
        return structureRepository.saveSite(site);
    }

    @Override
    public void deleteSite(UUID etablissementId, UUID siteId) {
        get(etablissementId);
        structureRepository.deleteSite(siteId, etablissementId);
    }

    @Override
    public Filiere addFiliere(UUID etablissementId, Filiere filiere) {
        get(etablissementId);
        filiere.setIdFiliere(UUID.randomUUID());
        filiere.setEtablissementId(etablissementId);
        return structureRepository.saveFiliere(filiere);
    }

    @Override
    public List<Filiere> listFilieres(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findFilieres(etablissementId);
    }

    @Override
    public Filiere getFiliere(UUID etablissementId, UUID filiereId) {
        get(etablissementId);
        return structureRepository.findFiliere(filiereId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("Filiere", filiereId));
    }

    @Override
    public Filiere updateFiliere(UUID etablissementId, UUID filiereId, Filiere filiere) {
        getFiliere(etablissementId, filiereId);
        filiere.setIdFiliere(filiereId);
        filiere.setEtablissementId(etablissementId);
        return structureRepository.saveFiliere(filiere);
    }

    @Override
    public void deleteFiliere(UUID etablissementId, UUID filiereId) {
        get(etablissementId);
        structureRepository.deleteFiliere(filiereId, etablissementId);
    }

    @Override
    public Programme addProgramme(UUID etablissementId, Programme programme) {
        get(etablissementId);
        programme.setIdProgramme(UUID.randomUUID());
        return structureRepository.saveProgramme(programme);
    }

    @Override
    public List<Programme> listProgrammes(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findProgrammes(etablissementId);
    }

    @Override
    public Programme getProgramme(UUID etablissementId, UUID programmeId) {
        get(etablissementId);
        return structureRepository.findProgramme(programmeId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("Programme", programmeId));
    }

    @Override
    public Programme updateProgramme(UUID etablissementId, UUID programmeId, Programme programme) {
        getProgramme(etablissementId, programmeId);
        programme.setIdProgramme(programmeId);
        return structureRepository.saveProgramme(programme);
    }

    @Override
    public void deleteProgramme(UUID etablissementId, UUID programmeId) {
        get(etablissementId);
        structureRepository.deleteProgramme(programmeId, etablissementId);
    }

    @Override
    public Cycle addCycle(UUID etablissementId, Cycle cycle) {
        get(etablissementId);
        cycle.setId(UUID.randomUUID());
        cycle.setEtablissementId(etablissementId);
        return structureRepository.saveCycle(cycle);
    }

    @Override
    public List<Cycle> listCycles(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findCycles(etablissementId);
    }

    @Override
    public Cycle getCycle(UUID etablissementId, UUID cycleId) {
        get(etablissementId);
        return structureRepository.findCycle(cycleId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("Cycle", cycleId));
    }

    @Override
    public Cycle updateCycle(UUID etablissementId, UUID cycleId, Cycle cycle) {
        getCycle(etablissementId, cycleId);
        cycle.setId(cycleId);
        cycle.setEtablissementId(etablissementId);
        return structureRepository.saveCycle(cycle);
    }

    @Override
    public void deleteCycle(UUID etablissementId, UUID cycleId) {
        get(etablissementId);
        structureRepository.deleteCycle(cycleId, etablissementId);
    }

    @Override
    public Parametre addParametre(UUID etablissementId, Parametre parametre) {
        get(etablissementId);
        parametre.setId(UUID.randomUUID());
        parametre.setEtablissementId(etablissementId);
        return structureRepository.saveParametre(parametre);
    }

    @Override
    public List<Parametre> listParametres(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findParametres(etablissementId);
    }

    @Override
    public Parametre getParametre(UUID etablissementId, UUID parametreId) {
        get(etablissementId);
        return structureRepository.findParametre(parametreId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("Parametre", parametreId));
    }

    @Override
    public Parametre updateParametre(UUID etablissementId, UUID parametreId, Parametre parametre) {
        getParametre(etablissementId, parametreId);
        parametre.setId(parametreId);
        parametre.setEtablissementId(etablissementId);
        return structureRepository.saveParametre(parametre);
    }

    @Override
    public void deleteParametre(UUID etablissementId, UUID parametreId) {
        get(etablissementId);
        structureRepository.deleteParametre(parametreId, etablissementId);
    }

    @Override
    public GrillePreset addPreset(UUID etablissementId, GrillePreset preset) {
        get(etablissementId);
        preset.setIdGrillePreset(UUID.randomUUID());
        preset.setEtablissementId(etablissementId);
        return structureRepository.savePreset(preset);
    }

    @Override
    public List<GrillePreset> listPresets(UUID etablissementId) {
        get(etablissementId);
        return structureRepository.findPresets(etablissementId);
    }

    @Override
    public GrillePreset getPreset(UUID etablissementId, UUID presetId) {
        get(etablissementId);
        return structureRepository.findPreset(presetId, etablissementId)
                .orElseThrow(() -> TenantException.notFound("GrillePreset", presetId));
    }

    @Override
    public GrillePreset dupliquerPreset(UUID etablissementId, UUID presetId, String libelle) {
        GrillePreset source = getPreset(etablissementId, presetId);
        GrillePreset copie = source.dupliquer(libelle);
        copie.setIdGrillePreset(UUID.randomUUID());
        copie.setEtablissementId(etablissementId);
        return structureRepository.savePreset(copie);
    }

    @Override
    public void deletePreset(UUID etablissementId, UUID presetId) {
        get(etablissementId);
        structureRepository.deletePreset(presetId, etablissementId);
    }

    private void creerReferentiels(UUID etablissementId, ProfilEtablissement profil) {
        int ordre = 0;
        for (NatureReferentiel nature : NatureReferentiel.values()) {
            Referentiel referentiel = new Referentiel();
            referentiel.setIdReferentiel(UUID.randomUUID());
            referentiel.setEtablissementId(etablissementId);
            referentiel.setCode(nature.name());
            referentiel.setLibelle(nature.name());
            referentiel.setNature(nature);
            referentiel.setOrdre(ordre++);
            referentiel.setActif(true);
            referentielRepository.save(referentiel);
        }
    }
}