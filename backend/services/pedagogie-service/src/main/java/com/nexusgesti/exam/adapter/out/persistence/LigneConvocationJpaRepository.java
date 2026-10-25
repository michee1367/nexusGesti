package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.LigneConvocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneConvocationJpaRepository extends JpaRepository<LigneConvocationEntity, UUID> {

    List<LigneConvocationEntity> findByConvocationId(UUID convocationId);

    void deleteByConvocationId(UUID convocationId);
}