package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteJpaRepository extends JpaRepository<NoteEntity, UUID> {

    List<NoteEntity> findByEvaluationId(UUID evaluationId);

    List<NoteEntity> findByEleveId(UUID eleveId);

    List<NoteEntity> findByEvaluationIdIn(List<UUID> evaluationIds);

    void deleteByEvaluationId(UUID evaluationId);
}
