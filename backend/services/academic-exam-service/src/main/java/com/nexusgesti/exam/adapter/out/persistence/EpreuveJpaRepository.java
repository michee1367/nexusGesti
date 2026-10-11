package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.EpreuveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EpreuveJpaRepository extends JpaRepository<EpreuveEntity, UUID> {

    List<EpreuveEntity> findBySessionId(UUID sessionId);
}