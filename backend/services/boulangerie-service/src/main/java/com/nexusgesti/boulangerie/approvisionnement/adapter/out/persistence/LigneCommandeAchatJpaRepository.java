package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.LigneCommandeAchatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneCommandeAchatJpaRepository extends JpaRepository<LigneCommandeAchatEntity, UUID> {

    List<LigneCommandeAchatEntity> findByCommandeId(UUID commandeId);

    void deleteByCommandeId(UUID commandeId);
}
