package com.nexusgesti.people.adapter.out.persistence;

import com.nexusgesti.people.adapter.out.persistence.entity.FichierJointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FichierJointJpaRepository extends JpaRepository<FichierJointEntity, UUID> {

    List<FichierJointEntity> findByDossierId(UUID dossierId);

    void deleteByDossierId(UUID dossierId);
}
