package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.CongeEtudeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CongeEtudeJpaRepository extends JpaRepository<CongeEtudeEntity, UUID> {

    List<CongeEtudeEntity> findByEleveId(UUID eleveId);

    void deleteByEleveId(UUID eleveId);
}
