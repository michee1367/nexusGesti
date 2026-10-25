package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.PieceIdentiteEnseignantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PieceIdentiteEnseignantJpaRepository extends JpaRepository<PieceIdentiteEnseignantEntity, UUID> {

    List<PieceIdentiteEnseignantEntity> findByEnseignantId(UUID enseignantId);

    void deleteByEnseignantId(UUID enseignantId);
}
