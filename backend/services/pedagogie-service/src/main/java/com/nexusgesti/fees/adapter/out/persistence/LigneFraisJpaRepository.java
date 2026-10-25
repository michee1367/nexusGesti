package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.LigneFraisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneFraisJpaRepository extends JpaRepository<LigneFraisEntity, UUID> {

    List<LigneFraisEntity> findByAvisId(UUID avisId);

    long deleteByAvisId(UUID avisId);
}