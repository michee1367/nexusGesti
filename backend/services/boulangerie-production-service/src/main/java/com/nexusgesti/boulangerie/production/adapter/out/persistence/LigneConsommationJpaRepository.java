package com.nexusgesti.boulangerie.production.adapter.out.persistence;

import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.LigneConsommationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneConsommationJpaRepository extends JpaRepository<LigneConsommationEntity, UUID> {

    List<LigneConsommationEntity> findByFourneeId(UUID fourneeId);

    void deleteByFourneeId(UUID fourneeId);
}
