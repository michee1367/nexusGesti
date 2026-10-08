package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.RegleAssiduiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegleAssiduiteJpaRepository extends JpaRepository<RegleAssiduiteEntity, UUID> {

    List<RegleAssiduiteEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}