package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.PaiementEspecesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaiementEspecesJpaRepository extends JpaRepository<PaiementEspecesEntity, UUID> {

    Optional<PaiementEspecesEntity> findByPaiementId(UUID paiementId);

    long deleteByPaiementId(UUID paiementId);
}