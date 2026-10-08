package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.CycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CycleJpaRepository extends JpaRepository<CycleEntity, UUID> {

    List<CycleEntity> findByEtablissementId(UUID etablissementId);

    Optional<CycleEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    void deleteByIdAndEtablissementId(UUID id, UUID etablissementId);
}