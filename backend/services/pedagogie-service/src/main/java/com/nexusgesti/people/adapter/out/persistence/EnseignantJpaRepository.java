package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.EnseignantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnseignantJpaRepository extends JpaRepository<EnseignantEntity, UUID> {
}
