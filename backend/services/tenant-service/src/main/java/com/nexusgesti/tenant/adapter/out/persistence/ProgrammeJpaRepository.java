package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ProgrammeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgrammeJpaRepository extends JpaRepository<ProgrammeEntity, UUID> {

    List<ProgrammeEntity> findByFiliereId(UUID filiereId);

    Optional<ProgrammeEntity> findByFiliereIdAndId(UUID filiereId, UUID id);

    void deleteByFiliereIdAndId(UUID filiereId, UUID id);
}