package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.SanctionDisciplinaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SanctionDisciplinaireJpaRepository extends JpaRepository<SanctionDisciplinaireEntity, UUID> {

    List<SanctionDisciplinaireEntity> findByEleveId(UUID eleveId);

    void deleteByEleveId(UUID eleveId);
}
