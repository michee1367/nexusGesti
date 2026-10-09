package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.AffectationEnseignantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AffectationEnseignantJpaRepository extends JpaRepository<AffectationEnseignantEntity, UUID> {

    List<AffectationEnseignantEntity> findByEnseignantId(UUID enseignantId);

    List<AffectationEnseignantEntity> findByClasseId(UUID classeId);

    void deleteByEnseignantId(UUID enseignantId);
}
