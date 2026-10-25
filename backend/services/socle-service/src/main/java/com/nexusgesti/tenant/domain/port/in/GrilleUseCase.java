package com.nexusgesti.tenant.domain.port.in;

import com.nexusgesti.tenant.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface GrilleUseCase {

    Grille creer(UUID etablissementId, String type, Grille grille);

    Grille get(UUID etablissementId, String type, UUID grilleId);

    Grille update(UUID etablissementId, String type, UUID grilleId, Grille grille);

    List<? extends Grille> list(UUID etablissementId, String type);

    Grille activer(UUID etablissementId, String type, UUID grilleId);

    Grille figer(UUID etablissementId, String type, UUID grilleId);

    Grille dupliquer(UUID etablissementId, String type, UUID grilleId, String libelle);

    Grille applicable(UUID etablissementId, String type, ProfilEtablissement profil, UUID anneeId);

    GrilleNotation addBareme(UUID etablissementId, UUID grilleId, Bareme bareme);
    List<Bareme> listBaremes(UUID etablissementId, UUID grilleId);
    GrilleNotation deleteBareme(UUID etablissementId, UUID grilleId, UUID baremeId);

    GrilleNotation addReglePonderation(UUID etablissementId, UUID grilleId, ReglePonderation regle);
    List<ReglePonderation> listReglesPonderation(UUID etablissementId, UUID grilleId);
    GrilleNotation deleteReglePonderation(UUID etablissementId, UUID grilleId, UUID regleId);

    GrilleNotation setConfigCascade(UUID etablissementId, UUID grilleId, ConfigCascade config);
    ConfigCascade getConfigCascade(UUID etablissementId, UUID grilleId);

    GrilleNotation addSeuilDecision(UUID etablissementId, UUID grilleId, SeuilDecision seuil);
    GrilleNotation deleteSeuilDecision(UUID etablissementId, UUID grilleId, UUID seuilId);

    GrilleNotation addRegleAssiduite(UUID etablissementId, UUID grilleId, RegleAssiduite regle);
    GrilleNotation deleteRegleAssiduite(UUID etablissementId, UUID grilleId, UUID regleId);

    GrilleNotation addMention(UUID etablissementId, UUID grilleId, Mention mention);
    GrilleNotation deleteMention(UUID etablissementId, UUID grilleId, UUID mentionId);

    GrilleNotation addAppreciation(UUID etablissementId, UUID grilleId, RegleAppreciation appreciation);
    GrilleNotation deleteAppreciation(UUID etablissementId, UUID grilleId, UUID appreciationId);

    AlerteStock addAlerte(UUID etablissementId, UUID grilleId, AlerteStock alerte);
    List<AlerteStock> listAlertes(UUID etablissementId, UUID grilleId);
    AlerteStock acquitterAlerte(UUID etablissementId, UUID grilleId, UUID alerteId, UUID acquitteur);
}