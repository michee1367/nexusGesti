package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.GrillePresetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrillePresetJpaRepository extends JpaRepository<GrillePresetEntity, UUID> {

    List<GrillePresetEntity> findByEtablissementId(UUID etablissementId);

    Optional<GrillePresetEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    void deleteByIdAndEtablissementId(UUID id, UUID etablissementId);
}