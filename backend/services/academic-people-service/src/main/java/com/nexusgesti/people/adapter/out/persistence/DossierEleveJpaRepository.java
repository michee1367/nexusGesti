package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.DossierEleveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DossierEleveJpaRepository extends JpaRepository<DossierEleveEntity, UUID> {

    Optional<DossierEleveEntity> findByEleveId(UUID eleveId);
}
