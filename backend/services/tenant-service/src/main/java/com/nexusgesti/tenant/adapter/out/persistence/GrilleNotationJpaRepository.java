package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.GrilleNotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrilleNotationJpaRepository extends JpaRepository<GrilleNotationEntity, UUID> {

    List<GrilleNotationEntity> findByEtablissementId(UUID etablissementId);

    Optional<GrilleNotationEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    Optional<GrilleNotationEntity> findFirstByEtablissementIdAndActiveTrueAndProfil(UUID etablissementId, String profil);

    Optional<GrilleNotationEntity> findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
            UUID etablissementId, String profil, UUID anneeId);

    @Modifying
    @Query("update GrilleNotationEntity g set g.active = false "
            + "where g.etablissementId = :etablissementId and g.code = :code "
            + "and g.id <> :aConserver and g.active = true")
    void desactiverAutres(UUID etablissementId, String code, UUID aConserver);
}