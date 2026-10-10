package com.nexusgesti.enrollment.adapter.out.persistence;

import com.nexusgesti.enrollment.adapter.out.persistence.entity.MembreGroupeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembreGroupeJpaRepository extends JpaRepository<MembreGroupeEntity, UUID> {

    List<MembreGroupeEntity> findByGroupeId(UUID groupeId);

    void deleteByGroupeId(UUID groupeId);
}
