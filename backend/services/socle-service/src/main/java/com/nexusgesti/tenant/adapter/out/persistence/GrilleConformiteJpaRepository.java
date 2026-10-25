package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.GrilleConformiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrilleConformiteJpaRepository extends JpaRepository<GrilleConformiteEntity, UUID> {

    List<GrilleConformiteEntity> findByEtablissementId(UUID etablissementId);

    Optional<GrilleConformiteEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    Optional<GrilleConformiteEntity> findFirstByEtablissementIdAndActiveTrueAndProfil(UUID etablissementId, String profil);

    Optional<GrilleConformiteEntity> findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
            UUID etablissementId, String profil, UUID anneeId);

    @Modifying
    @Query("update GrilleConformiteEntity g set g.active = false "
            + "where g.etablissementId = :etablissementId and g.code = :code "
            + "and g.id <> :aConserver and g.active = true")
    void desactiverAutres(UUID etablissementId, String code, UUID aConserver);
}