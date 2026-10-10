package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.LigneBulletinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneBulletinJpaRepository extends JpaRepository<LigneBulletinEntity, UUID> {

    List<LigneBulletinEntity> findByBulletinId(UUID bulletinId);

    void deleteByBulletinId(UUID bulletinId);
}
