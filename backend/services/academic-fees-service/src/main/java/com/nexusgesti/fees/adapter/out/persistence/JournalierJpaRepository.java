package com.nexusgesti.fees.adapter.out.persistence;

import com.nexusgesti.fees.adapter.out.persistence.entity.JournalierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JournalierJpaRepository extends JpaRepository<JournalierEntity, UUID> {

    List<JournalierEntity> findByDate(LocalDate date);

    List<JournalierEntity> findBySite(UUID site);
}