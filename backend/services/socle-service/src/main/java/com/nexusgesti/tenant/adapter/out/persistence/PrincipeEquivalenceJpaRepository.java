package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.PrincipeEquivalenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrincipeEquivalenceJpaRepository extends JpaRepository<PrincipeEquivalenceEntity, UUID> {

    List<PrincipeEquivalenceEntity> findByRegleEquivalenceId(UUID regleEquivalenceId);

    void deleteByRegleEquivalenceId(UUID regleEquivalenceId);
}