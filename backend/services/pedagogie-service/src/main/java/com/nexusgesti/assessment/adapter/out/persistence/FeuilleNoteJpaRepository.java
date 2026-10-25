package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.FeuilleNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeuilleNoteJpaRepository extends JpaRepository<FeuilleNoteEntity, UUID> {

    Optional<FeuilleNoteEntity> findByEvaluationId(UUID evaluationId);
}
