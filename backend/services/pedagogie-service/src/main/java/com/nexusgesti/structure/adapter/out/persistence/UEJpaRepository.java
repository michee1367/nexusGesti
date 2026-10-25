package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.UEEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UEJpaRepository extends JpaRepository<UEEntity, UUID> {
}
