package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.CreneauExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreneauExamenJpaRepository extends JpaRepository<CreneauExamenEntity, UUID> {

    List<CreneauExamenEntity> findBySessionId(UUID sessionId);
}