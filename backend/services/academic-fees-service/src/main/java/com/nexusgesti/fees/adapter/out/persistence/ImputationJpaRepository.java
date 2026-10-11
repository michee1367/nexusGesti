package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.ImputationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImputationJpaRepository extends JpaRepository<ImputationEntity, UUID> {

    List<ImputationEntity> findByPaiementId(UUID paiementId);

    long deleteByPaiementId(UUID paiementId);
}