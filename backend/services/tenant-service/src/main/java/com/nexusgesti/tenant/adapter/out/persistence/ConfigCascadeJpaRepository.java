package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ConfigCascadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfigCascadeJpaRepository extends JpaRepository<ConfigCascadeEntity, UUID> {

    Optional<ConfigCascadeEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}