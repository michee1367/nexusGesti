package com.nexusgesti.boulangerie.production.adapter.out.persistence;

import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.PerteProductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PerteProductionJpaRepository extends JpaRepository<PerteProductionEntity, UUID> {

    List<PerteProductionEntity> findByFourneeId(UUID fourneeId);

    void deleteByFourneeId(UUID fourneeId);
}
