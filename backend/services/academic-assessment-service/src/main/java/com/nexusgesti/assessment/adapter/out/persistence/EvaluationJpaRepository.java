package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, UUID> {

    List<EvaluationEntity> findByClasseId(UUID classeId);

    List<EvaluationEntity> findByPeriodeId(UUID periodeId);
}
