package com.nexusgesti.tenant.adapter.out.persistence;

import com.nexusgesti.tenant.adapter.out.persistence.entity.AnneeScolaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnneeScolaireJpaRepository extends JpaRepository<AnneeScolaireEntity, UUID> {

    List<AnneeScolaireEntity> findByEtablissementId(UUID etablissementId);

    Optional<AnneeScolaireEntity> findByIdAndEtablissementId(UUID id, UUID etablissementId);
}