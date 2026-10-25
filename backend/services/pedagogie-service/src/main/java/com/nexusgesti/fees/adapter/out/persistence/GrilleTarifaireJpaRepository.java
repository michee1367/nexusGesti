package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.GrilleTarifaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GrilleTarifaireJpaRepository extends JpaRepository<GrilleTarifaireEntity, UUID> {
}