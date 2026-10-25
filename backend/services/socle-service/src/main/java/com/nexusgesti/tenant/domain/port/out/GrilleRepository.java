package com.nexusgesti.tenant.domain.port.out;

import com.nexusgesti.tenant.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrilleRepository {

    GrilleNotation saveNotation(GrilleNotation grille);
    Optional<GrilleNotation> findNotation(UUID id);
    List<GrilleNotation> listNotations(UUID etablissementId);
    Optional<GrilleNotation> findNotationApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId);

    GrilleStock saveStock(GrilleStock grille);
    Optional<GrilleStock> findStock(UUID id);
    List<GrilleStock> listStocks(UUID etablissementId);
    Optional<GrilleStock> findStockApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId);

    GrilleConformite saveConformite(GrilleConformite grille);
    Optional<GrilleConformite> findConformite(UUID id);
    List<GrilleConformite> listConformites(UUID etablissementId);
    Optional<GrilleConformite> findConformiteApplicable(UUID etablissementId, ProfilEtablissement profil, UUID anneeId);

    AlerteStock saveAlerte(AlerteStock alerte);
    List<AlerteStock> findAlertes(UUID grilleId);
    Optional<AlerteStock> findAlerte(UUID grilleId, UUID alerteId);

    void desactiverAutresActives(UUID etablissementId, String typeGrille, String code, UUID aConserver);
}