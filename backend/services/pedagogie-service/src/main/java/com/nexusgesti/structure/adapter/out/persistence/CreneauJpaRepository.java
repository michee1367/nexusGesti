package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.CreneauEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreneauJpaRepository extends JpaRepository<CreneauEntity, UUID> {
}
