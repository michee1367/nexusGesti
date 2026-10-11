package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.PaiementVirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaiementVirementJpaRepository extends JpaRepository<PaiementVirementEntity, UUID> {

    Optional<PaiementVirementEntity> findByPaiementId(UUID paiementId);

    long deleteByPaiementId(UUID paiementId);
}