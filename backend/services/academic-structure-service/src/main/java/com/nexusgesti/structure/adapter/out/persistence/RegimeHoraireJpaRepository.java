package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.RegimeHoraireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegimeHoraireJpaRepository extends JpaRepository<RegimeHoraireEntity, UUID> {

    Optional<RegimeHoraireEntity> findByCode(String code);
}
