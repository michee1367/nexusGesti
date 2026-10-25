package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.NoteExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteExamenJpaRepository extends JpaRepository<NoteExamenEntity, UUID> {

    List<NoteExamenEntity> findByEpreuveId(UUID epreuveId);

    List<NoteExamenEntity> findByEleveId(UUID eleveId);
}