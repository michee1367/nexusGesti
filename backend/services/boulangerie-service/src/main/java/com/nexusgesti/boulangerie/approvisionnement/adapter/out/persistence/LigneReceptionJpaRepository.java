package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.LigneReceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneReceptionJpaRepository extends JpaRepository<LigneReceptionEntity, UUID> {

    List<LigneReceptionEntity> findByReceptionId(UUID receptionId);

    void deleteByReceptionId(UUID receptionId);
}
