package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.PeriodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeriodeJpaRepository extends JpaRepository<PeriodeEntity, UUID> {

    List<PeriodeEntity> findByAnneeId(UUID anneeId);

    Optional<PeriodeEntity> findByIdAndAnneeId(UUID id, UUID anneeId);
}