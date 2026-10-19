package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.CategorieProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategorieProduitJpaRepository extends JpaRepository<CategorieProduitEntity, UUID> {
}
