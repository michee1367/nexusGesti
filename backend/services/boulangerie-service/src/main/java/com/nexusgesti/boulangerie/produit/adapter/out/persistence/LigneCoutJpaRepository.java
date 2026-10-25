package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.LigneCoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneCoutJpaRepository extends JpaRepository<LigneCoutEntity, UUID> {

    List<LigneCoutEntity> findByRecetteId(UUID recetteId);

    void deleteByRecetteId(UUID recetteId);
}
