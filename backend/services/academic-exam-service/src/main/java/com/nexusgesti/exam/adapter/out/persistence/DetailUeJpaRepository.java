package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.DetailUeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetailUeJpaRepository extends JpaRepository<DetailUeEntity, UUID> {

    List<DetailUeEntity> findByDecisionUeId(UUID decisionUeId);

    void deleteByDecisionUeId(UUID decisionUeId);
}