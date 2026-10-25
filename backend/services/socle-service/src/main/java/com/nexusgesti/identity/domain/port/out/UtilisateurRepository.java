package com.nexusgesti.identity.domain.port.out;

import com.nexusgesti.identity.domain.model.MotifDesactivation;
import com.nexusgesti.identity.domain.model.Utilisateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository {

    Utilisateur save(Utilisateur utilisateur);

    Optional<Utilisateur> findById(UUID id);

    Optional<Utilisateur> findByKeycloakUserId(UUID keycloakUserId);

    boolean existsByEmail(String email);

    List<Utilisateur> search(String query);

    MotifDesactivation saveMotifDesactivation(UUID utilisateurId, MotifDesactivation motif);

    List<MotifDesactivation> findMotifsDesactivation(UUID utilisateurId);
}
