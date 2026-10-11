package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.DecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DecisionJpaRepository extends JpaRepository<DecisionEntity, UUID> {

    List<DecisionEntity> findByDeliberationId(UUID deliberationId);

    void deleteByDeliberationId(UUID deliberationId);
}