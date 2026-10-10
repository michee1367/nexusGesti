package com.nexusgesti.attendance.adapter.out.persistence;

import com.nexusgesti.attendance.adapter.out.persistence.entity.FeuillePresenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeuillePresenceJpaRepository extends JpaRepository<FeuillePresenceEntity, UUID> {

    List<FeuillePresenceEntity> findByClasseId(UUID classeId);

    List<FeuillePresenceEntity> findByDate(LocalDate date);
}
