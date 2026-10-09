package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.TitulariteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TitulariteJpaRepository extends JpaRepository<TitulariteEntity, UUID> {

    List<TitulariteEntity> findByClasseId(UUID classeId);
}
