package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.ReductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReductionJpaRepository extends JpaRepository<ReductionEntity, UUID> {

    List<ReductionEntity> findByGrilleId(UUID grilleId);

    long deleteByGrilleId(UUID grilleId);
}