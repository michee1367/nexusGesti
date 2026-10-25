package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.LigneVenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneVenteJpaRepository extends JpaRepository<LigneVenteEntity, UUID> {

    List<LigneVenteEntity> findByVenteId(UUID venteId);

    void deleteByVenteId(UUID venteId);
}
