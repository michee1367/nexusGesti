package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.GrilleStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrilleStockJpaRepository extends JpaRepository<GrilleStockEntity, UUID> {

    List<GrilleStockEntity> findByEtablissementId(UUID etablissementId);

    Optional<GrilleStockEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    Optional<GrilleStockEntity> findFirstByEtablissementIdAndActiveTrueAndProfil(UUID etablissementId, String profil);

    Optional<GrilleStockEntity> findFirstByEtablissementIdAndActiveTrueAndProfilAndAnneeId(
            UUID etablissementId, String profil, UUID anneeId);

    @Modifying
    @Query("update GrilleStockEntity g set g.active = false "
            + "where g.etablissementId = :etablissementId and g.code = :code "
            + "and g.id <> :aConserver and g.active = true")
    void desactiverAutres(UUID etablissementId, String code, UUID aConserver);
}