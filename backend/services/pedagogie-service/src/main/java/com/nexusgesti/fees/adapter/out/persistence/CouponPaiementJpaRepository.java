package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.CouponPaiementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponPaiementJpaRepository extends JpaRepository<CouponPaiementEntity, UUID> {

    Optional<CouponPaiementEntity> findByNumero(String numero);

    long countByAnnee(UUID annee);
}