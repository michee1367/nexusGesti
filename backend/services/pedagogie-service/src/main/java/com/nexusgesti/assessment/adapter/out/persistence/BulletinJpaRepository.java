package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.BulletinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BulletinJpaRepository extends JpaRepository<BulletinEntity, UUID> {

    List<BulletinEntity> findByEleveId(UUID eleveId);

    List<BulletinEntity> findByClasseId(UUID classeId);

    List<BulletinEntity> findByPeriodeId(UUID periodeId);
}
