package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.CouponNotesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CouponNotesJpaRepository extends JpaRepository<CouponNotesEntity, UUID> {

    List<CouponNotesEntity> findByBulletinId(UUID bulletinId);

    long countByAnneeId(UUID anneeId);
}
