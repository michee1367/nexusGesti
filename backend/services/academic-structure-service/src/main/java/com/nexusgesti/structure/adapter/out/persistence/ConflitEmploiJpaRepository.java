package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.ConflitEmploiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConflitEmploiJpaRepository extends JpaRepository<ConflitEmploiEntity, UUID> {

    List<ConflitEmploiEntity> findByEmploiId(UUID emploiId);

    void deleteByEmploiId(UUID emploiId);
}
