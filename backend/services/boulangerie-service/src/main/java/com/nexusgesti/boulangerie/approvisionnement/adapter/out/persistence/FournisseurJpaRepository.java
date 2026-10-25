package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.FournisseurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FournisseurJpaRepository extends JpaRepository<FournisseurEntity, UUID> {
}
