package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.VenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VenteJpaRepository extends JpaRepository<VenteEntity, UUID> {

    List<VenteEntity> findBySessionIdOrderByDateAsc(UUID sessionId);

    long countBySessionId(UUID sessionId);
}
