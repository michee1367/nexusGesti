package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.ResultatOfficielEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResultatOfficielJpaRepository extends JpaRepository<ResultatOfficielEntity, UUID> {

    List<ResultatOfficielEntity> findByDecisionId(UUID decisionId);
}