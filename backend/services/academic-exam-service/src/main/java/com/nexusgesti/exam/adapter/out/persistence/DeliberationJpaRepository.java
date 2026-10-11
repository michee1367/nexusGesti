package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.DeliberationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliberationJpaRepository extends JpaRepository<DeliberationEntity, UUID> {

    List<DeliberationEntity> findBySessionId(UUID sessionId);
}