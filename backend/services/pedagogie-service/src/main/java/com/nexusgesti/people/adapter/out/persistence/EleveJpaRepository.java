package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.EleveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EleveJpaRepository extends JpaRepository<EleveEntity, UUID> {
}
