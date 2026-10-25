package com.nexusgesti.tenant.domain.port.in;

import com.nexusgesti.tenant.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface EtablissementUseCase {

    Etablissement creer(UUID id, Etablissement etablissement);

    Etablissement get(UUID id);

    Etablissement changerStatut(UUID id, StatutEtablissement cible, String motif);

    Etablissement getEtablissement(UUID etablissementId);

    Site addSite(UUID etablissementId, Site site);
    List<Site> listSites(UUID etablissementId);
    Site getSite(UUID etablissementId, UUID siteId);
    Site updateSite(UUID etablissementId, UUID siteId, Site site);
    void deleteSite(UUID etablissementId, UUID siteId);

    Filiere addFiliere(UUID etablissementId, Filiere filiere);
    List<Filiere> listFilieres(UUID etablissementId);
    Filiere getFiliere(UUID etablissementId, UUID filiereId);
    Filiere updateFiliere(UUID etablissementId, UUID filiereId, Filiere filiere);
    void deleteFiliere(UUID etablissementId, UUID filiereId);

    Programme addProgramme(UUID etablissementId, Programme programme);
    List<Programme> listProgrammes(UUID etablissementId);
    Programme getProgramme(UUID etablissementId, UUID programmeId);
    Programme updateProgramme(UUID etablissementId, UUID programmeId, Programme programme);
    void deleteProgramme(UUID etablissementId, UUID programmeId);

    Cycle addCycle(UUID etablissementId, Cycle cycle);
    List<Cycle> listCycles(UUID etablissementId);
    Cycle getCycle(UUID etablissementId, UUID cycleId);
    Cycle updateCycle(UUID etablissementId, UUID cycleId, Cycle cycle);
    void deleteCycle(UUID etablissementId, UUID cycleId);

    Parametre addParametre(UUID etablissementId, Parametre parametre);
    List<Parametre> listParametres(UUID etablissementId);
    Parametre getParametre(UUID etablissementId, UUID parametreId);
    Parametre updateParametre(UUID etablissementId, UUID parametreId, Parametre parametre);
    void deleteParametre(UUID etablissementId, UUID parametreId);

    GrillePreset addPreset(UUID etablissementId, GrillePreset preset);
    List<GrillePreset> listPresets(UUID etablissementId);
    GrillePreset getPreset(UUID etablissementId, UUID presetId);
    GrillePreset dupliquerPreset(UUID etablissementId, UUID presetId, String libelle);
    void deletePreset(UUID etablissementId, UUID presetId);
}