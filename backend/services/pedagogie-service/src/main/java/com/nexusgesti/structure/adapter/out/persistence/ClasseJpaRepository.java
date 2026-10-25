package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.ClasseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClasseJpaRepository extends JpaRepository<ClasseEntity, UUID> {
}
