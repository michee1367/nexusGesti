package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.LigneGrilleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneGrilleJpaRepository extends JpaRepository<LigneGrilleEntity, UUID> {

    List<LigneGrilleEntity> findByGrilleId(UUID grilleId);

    long deleteByGrilleId(UUID grilleId);
}