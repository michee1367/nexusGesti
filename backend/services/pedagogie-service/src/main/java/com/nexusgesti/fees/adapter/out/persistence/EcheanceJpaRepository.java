package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.EcheanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EcheanceJpaRepository extends JpaRepository<EcheanceEntity, UUID> {

    List<EcheanceEntity> findByLigneId(UUID ligneId);

    long deleteByLigneId(UUID ligneId);
}