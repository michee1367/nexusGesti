package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.RemboursementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RemboursementJpaRepository extends JpaRepository<RemboursementEntity, UUID> {

    Optional<RemboursementEntity> findByPaiementId(UUID paiementId);

    long deleteByPaiementId(UUID paiementId);
}