package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.BaremeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BaremeJpaRepository extends JpaRepository<BaremeEntity, UUID> {

    List<BaremeEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}