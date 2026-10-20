package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.CommandeAchatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommandeAchatJpaRepository extends JpaRepository<CommandeAchatEntity, UUID> {
}
