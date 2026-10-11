package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.PaiementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaiementJpaRepository extends JpaRepository<PaiementEntity, UUID> {
}