package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.LienParentaliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LienParentaliteJpaRepository extends JpaRepository<LienParentaliteEntity, UUID> {

    List<LienParentaliteEntity> findByEleveId(UUID eleveId);
}
