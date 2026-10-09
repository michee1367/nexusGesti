package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.CoursECUEEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoursECUEJpaRepository extends JpaRepository<CoursECUEEntity, UUID> {
}
