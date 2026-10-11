package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.RattrapageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RattrapageJpaRepository extends JpaRepository<RattrapageEntity, UUID> {

    List<RattrapageEntity> findByEleveId(UUID eleveId);
}