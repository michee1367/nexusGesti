package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.MatiereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatiereJpaRepository extends JpaRepository<MatiereEntity, UUID> {
}
