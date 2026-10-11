package com.nexusgesti.exam.adapter.out.persistence;

import com.nexusgesti.exam.adapter.out.persistence.entity.SignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SignatureJpaRepository extends JpaRepository<SignatureEntity, UUID> {

    List<SignatureEntity> findByProcesVerbalId(UUID procesVerbalId);

    void deleteByProcesVerbalId(UUID procesVerbalId);
}