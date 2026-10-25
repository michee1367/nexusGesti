package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.identity.adapter.out.persistence.entity.UtilisateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurJpaRepository extends JpaRepository<UtilisateurEntity, UUID> {

    Optional<UtilisateurEntity> findByKeycloakUserId(UUID keycloakUserId);

    boolean existsByEmail(String email);

    List<UtilisateurEntity> findByEmailContainingIgnoreCaseOrNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String email, String nom, String prenom);
}
