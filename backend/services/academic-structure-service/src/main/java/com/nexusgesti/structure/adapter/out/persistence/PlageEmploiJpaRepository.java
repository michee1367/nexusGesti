package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.PlageEmploiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlageEmploiJpaRepository extends JpaRepository<PlageEmploiEntity, UUID> {

    List<PlageEmploiEntity> findByEmploiId(UUID emploiId);

    void deleteByEmploiId(UUID emploiId);
}
