package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.AvisPaiementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvisPaiementJpaRepository extends JpaRepository<AvisPaiementEntity, UUID> {
}