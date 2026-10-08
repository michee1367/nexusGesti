package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.RegleEquivalenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegleEquivalenceJpaRepository extends JpaRepository<RegleEquivalenceEntity, UUID> {

    List<RegleEquivalenceEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}