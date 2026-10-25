package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.PaiementVenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaiementVenteJpaRepository extends JpaRepository<PaiementVenteEntity, UUID> {

    List<PaiementVenteEntity> findByVenteId(UUID venteId);

    void deleteByVenteId(UUID venteId);
}
