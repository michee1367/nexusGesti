package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.SalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalleJpaRepository extends JpaRepository<SalleEntity, UUID> {
}
