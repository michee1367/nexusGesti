package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.GammeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GammeJpaRepository extends JpaRepository<GammeEntity, UUID> {
}
