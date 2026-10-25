package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.AlerteStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlerteStockJpaRepository extends JpaRepository<AlerteStockEntity, UUID> {

    List<AlerteStockEntity> findByGrilleId(UUID grilleId);

    Optional<AlerteStockEntity> findByIdAndGrilleId(UUID id, UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}