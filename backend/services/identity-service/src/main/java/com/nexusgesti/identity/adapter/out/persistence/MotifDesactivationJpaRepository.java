package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.identity.adapter.out.persistence.entity.MotifDesactivationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MotifDesactivationJpaRepository extends JpaRepository<MotifDesactivationEntity, UUID> {

    List<MotifDesactivationEntity> findByUtilisateurIdOrderByDateDesc(UUID utilisateurId);
}
