package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.PaiementMobileMoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaiementMobileMoneyJpaRepository extends JpaRepository<PaiementMobileMoneyEntity, UUID> {

    Optional<PaiementMobileMoneyEntity> findByPaiementId(UUID paiementId);

    long deleteByPaiementId(UUID paiementId);
}