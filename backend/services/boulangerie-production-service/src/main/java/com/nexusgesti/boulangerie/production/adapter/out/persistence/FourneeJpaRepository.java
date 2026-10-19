package com.nexusgesti.boulangerie.production.adapter.out.persistence;

import com.nexusgesti.boulangerie.production.adapter.out.persistence.entity.FourneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FourneeJpaRepository extends JpaRepository<FourneeEntity, UUID> {
}
