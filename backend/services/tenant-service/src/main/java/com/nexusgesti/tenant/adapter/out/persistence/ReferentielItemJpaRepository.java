package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.ReferentielItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferentielItemJpaRepository extends JpaRepository<ReferentielItemEntity, UUID> {

    List<ReferentielItemEntity> findByReferentielId(UUID referentielId);

    Optional<ReferentielItemEntity> findByIdAndReferentielId(UUID id, UUID referentielId);

    void deleteByIdAndReferentielId(UUID id, UUID referentielId);
}