package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.AcompteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcompteJpaRepository extends JpaRepository<AcompteEntity, UUID> {

    List<AcompteEntity> findByCommandeId(UUID commandeId);

    void deleteByCommandeId(UUID commandeId);
}
