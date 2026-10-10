package com.nexusgesti.enrollment.adapter.out.persistence;

import com.nexusgesti.enrollment.adapter.out.persistence.entity.RegleInscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegleInscriptionJpaRepository extends JpaRepository<RegleInscriptionEntity, UUID> {
}
