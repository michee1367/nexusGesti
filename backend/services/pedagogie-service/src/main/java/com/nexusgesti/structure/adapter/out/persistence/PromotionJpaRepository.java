package com.nexusgesti.structure.adapter.out.persistence;

import com.nexusgesti.structure.adapter.out.persistence.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromotionJpaRepository extends JpaRepository<PromotionEntity, UUID> {
}
