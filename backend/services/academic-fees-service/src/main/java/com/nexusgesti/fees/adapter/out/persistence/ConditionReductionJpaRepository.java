package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.ConditionReductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConditionReductionJpaRepository extends JpaRepository<ConditionReductionEntity, UUID> {

    List<ConditionReductionEntity> findByReductionId(UUID reductionId);

    long deleteByReductionId(UUID reductionId);
}