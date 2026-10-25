package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.ConvocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConvocationJpaRepository extends JpaRepository<ConvocationEntity, UUID> {

    List<ConvocationEntity> findBySessionId(UUID sessionId);

    long countBySessionId(UUID sessionId);
}