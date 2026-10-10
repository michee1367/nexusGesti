package com.nexusgesti.enrollment.adapter.out.persistence;

import com.nexusgesti.enrollment.adapter.out.persistence.entity.InscriptionUEEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InscriptionUEJpaRepository extends JpaRepository<InscriptionUEEntity, UUID> {

    List<InscriptionUEEntity> findByEleveId(UUID eleveId);
}
