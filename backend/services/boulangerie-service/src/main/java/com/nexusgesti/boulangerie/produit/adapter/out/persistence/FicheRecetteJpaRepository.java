package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.FicheRecetteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FicheRecetteJpaRepository extends JpaRepository<FicheRecetteEntity, UUID> {

    @Query("select coalesce(max(r.version), 0) from FicheRecetteEntity r where r.code = :code")
    int maxVersion(@Param("code") String code);

    @Query("select count(r) from FicheRecetteEntity r where r.produitCode = :produitCode and r.active = true")
    int countActives(@Param("produitCode") String produitCode);

    Optional<FicheRecetteEntity> findByCodeAndVersion(String code, Integer version);

    Optional<FicheRecetteEntity> findFirstByCodeAndActiveTrueOrderByVersionDesc(String code);
}
