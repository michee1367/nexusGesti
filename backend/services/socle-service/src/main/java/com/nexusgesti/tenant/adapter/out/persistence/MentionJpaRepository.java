package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.MentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MentionJpaRepository extends JpaRepository<MentionEntity, UUID> {

    List<MentionEntity> findByGrilleId(UUID grilleId);

    void deleteByGrilleId(UUID grilleId);
}