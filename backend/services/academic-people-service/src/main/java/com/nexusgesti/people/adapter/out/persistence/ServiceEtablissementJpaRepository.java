package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.ServiceEtablissementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceEtablissementJpaRepository extends JpaRepository<ServiceEtablissementEntity, UUID> {

    Optional<ServiceEtablissementEntity> findByCode(String code);
}
