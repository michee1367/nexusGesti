package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.SessionExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionExamenJpaRepository extends JpaRepository<SessionExamenEntity, UUID> {

    List<SessionExamenEntity> findByAnneeId(UUID anneeId);

    List<SessionExamenEntity> findByClasseId(UUID classeId);
}