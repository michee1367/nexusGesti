package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.ReceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReceptionJpaRepository extends JpaRepository<ReceptionEntity, UUID> {

    List<ReceptionEntity> findByCommandeId(UUID commandeId);
}
