package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.SessionCaisseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionCaisseJpaRepository extends JpaRepository<SessionCaisseEntity, UUID> {

    Optional<SessionCaisseEntity> findFirstByClotureeFalse();
}
