package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ReferentielEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferentielJpaRepository extends JpaRepository<ReferentielEntity, UUID> {

    List<ReferentielEntity> findByEtablissementId(UUID etablissementId);

    Optional<ReferentielEntity> findByEtablissementIdAndCode(UUID etablissementId, String code);

    Optional<ReferentielEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);
}