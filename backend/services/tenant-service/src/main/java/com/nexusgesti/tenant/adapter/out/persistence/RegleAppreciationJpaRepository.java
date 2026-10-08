package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.RegleAppreciationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegleAppreciationJpaRepository extends JpaRepository<RegleAppreciationEntity, UUID> {

    List<RegleAppreciationEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}