package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.FiliereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FiliereJpaRepository extends JpaRepository<FiliereEntity, UUID> {

    List<FiliereEntity> findByEtablissementId(UUID etablissementId);

    Optional<FiliereEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    void deleteByIdAndEtablissementId(UUID id, UUID etablissementId);
}