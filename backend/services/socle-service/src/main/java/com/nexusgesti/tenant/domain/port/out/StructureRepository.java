package com.nexusgesti.tenant.domain.port.out;

import com.nexusgesti.tenant.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureRepository {

    Site saveSite(Site site);
    List<Site> findSites(UUID etablissementId);
    Optional<Site> findSite(UUID id, UUID etablissementId);
    void deleteSite(UUID id, UUID etablissementId);

    Filiere saveFiliere(Filiere filiere);
    List<Filiere> findFilieres(UUID etablissementId);
    Optional<Filiere> findFiliere(UUID id, UUID etablissementId);
    void deleteFiliere(UUID id, UUID etablissementId);

    Programme saveProgramme(Programme programme);
    List<Programme> findProgrammes(UUID etablissementId);
    Optional<Programme> findProgramme(UUID id, UUID etablissementId);
    void deleteProgramme(UUID id, UUID etablissementId);

    Cycle saveCycle(Cycle cycle);
    List<Cycle> findCycles(UUID etablissementId);
    Optional<Cycle> findCycle(UUID id, UUID etablissementId);
    void deleteCycle(UUID id, UUID etablissementId);

    Parametre saveParametre(Parametre parametre);
    List<Parametre> findParametres(UUID etablissementId);
    Optional<Parametre> findParametre(UUID id, UUID etablissementId);
    void deleteParametre(UUID id, UUID etablissementId);

    GrillePreset savePreset(GrillePreset preset);
    List<GrillePreset> findPresets(UUID etablissementId);
    Optional<GrillePreset> findPreset(UUID id, UUID etablissementId);
    void deletePreset(UUID id, UUID etablissementId);
}