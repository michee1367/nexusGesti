package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.NiveauEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NiveauJpaRepository extends JpaRepository<NiveauEntity, UUID> {
}
