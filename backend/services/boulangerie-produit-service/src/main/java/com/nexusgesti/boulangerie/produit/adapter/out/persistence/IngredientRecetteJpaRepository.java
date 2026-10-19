package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.IngredientRecetteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IngredientRecetteJpaRepository extends JpaRepository<IngredientRecetteEntity, UUID> {

    List<IngredientRecetteEntity> findByRecetteId(UUID recetteId);

    void deleteByRecetteId(UUID recetteId);
}
