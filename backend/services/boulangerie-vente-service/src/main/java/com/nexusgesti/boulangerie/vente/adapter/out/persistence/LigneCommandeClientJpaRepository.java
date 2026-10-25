package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.LigneCommandeClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneCommandeClientJpaRepository extends JpaRepository<LigneCommandeClientEntity, UUID> {

    List<LigneCommandeClientEntity> findByCommandeId(UUID commandeId);

    void deleteByCommandeId(UUID commandeId);
}
