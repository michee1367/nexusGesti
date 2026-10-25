package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.CommandeClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommandeClientJpaRepository extends JpaRepository<CommandeClientEntity, UUID> {
}
