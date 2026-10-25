package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.ProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProduitJpaRepository extends JpaRepository<ProduitEntity, UUID> {
}
