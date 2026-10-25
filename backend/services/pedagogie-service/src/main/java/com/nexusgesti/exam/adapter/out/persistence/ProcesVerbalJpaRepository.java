package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.ProcesVerbalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProcesVerbalJpaRepository extends JpaRepository<ProcesVerbalEntity, UUID> {

    List<ProcesVerbalEntity> findBySessionId(UUID sessionId);
}