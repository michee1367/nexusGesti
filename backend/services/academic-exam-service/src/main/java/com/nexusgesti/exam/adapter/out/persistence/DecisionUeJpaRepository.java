package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.DecisionUeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DecisionUeJpaRepository extends JpaRepository<DecisionUeEntity, UUID> {

    List<DecisionUeEntity> findByDeliberationId(UUID deliberationId);

    void deleteByDeliberationId(UUID deliberationId);
}