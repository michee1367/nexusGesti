package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.CreneauEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreneauJpaRepository extends JpaRepository<CreneauEntity, UUID> {

    List<CreneauEntity> findBySessionId(UUID sessionId);
}