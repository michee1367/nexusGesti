package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.EtablissementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EtablissementJpaRepository extends JpaRepository<EtablissementEntity, UUID> {
}