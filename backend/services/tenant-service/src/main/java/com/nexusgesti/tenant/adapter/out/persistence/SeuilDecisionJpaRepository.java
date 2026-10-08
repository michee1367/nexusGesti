package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.SeuilDecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeuilDecisionJpaRepository extends JpaRepository<SeuilDecisionEntity, UUID> {

    List<SeuilDecisionEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}