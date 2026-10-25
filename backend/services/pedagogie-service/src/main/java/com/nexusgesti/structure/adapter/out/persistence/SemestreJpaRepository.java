package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.SemestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SemestreJpaRepository extends JpaRepository<SemestreEntity, UUID> {
}
