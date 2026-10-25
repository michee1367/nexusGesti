package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ReglePonderationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReglePonderationJpaRepository extends JpaRepository<ReglePonderationEntity, UUID> {

    List<ReglePonderationEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}