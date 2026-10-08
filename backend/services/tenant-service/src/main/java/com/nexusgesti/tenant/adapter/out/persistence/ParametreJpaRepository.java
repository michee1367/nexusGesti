package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ParametreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParametreJpaRepository extends JpaRepository<ParametreEntity, UUID> {

    List<ParametreEntity> findByEtablissementId(UUID etablissementId);

    Optional<ParametreEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);

    void deleteByIdAndEtablissementId(UUID id, UUID etablissementId);
}