package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.CorrectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CorrectionJpaRepository extends JpaRepository<CorrectionEntity, UUID> {

    List<CorrectionEntity> findByEpreuveId(UUID epreuveId);
}