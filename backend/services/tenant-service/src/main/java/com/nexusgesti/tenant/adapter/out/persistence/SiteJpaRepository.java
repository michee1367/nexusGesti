package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SiteJpaRepository extends JpaRepository<SiteEntity, UUID> {

    List<SiteEntity> findByEtablissementId(UUID etablissementId);

    Optional<SiteEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    void deleteByIdAndEtablissementId(UUID id, UUID etablissementId);
}