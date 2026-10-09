package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.EmploiDuTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmploiDuTempsJpaRepository extends JpaRepository<EmploiDuTempsEntity, UUID> {
}
