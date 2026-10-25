package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgentJpaRepository extends JpaRepository<AgentEntity, UUID> {
}
