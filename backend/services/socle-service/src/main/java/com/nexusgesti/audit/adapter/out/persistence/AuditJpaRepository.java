package com.nexusgesti.audit.adapter.out.persistence;

import com.nexusgesti.audit.adapter.out.persistence.entity.AuditEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AuditJpaRepository extends JpaRepository<AuditEntryEntity, UUID>, JpaSpecificationExecutor<AuditEntryEntity> {

    List<AuditEntryEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);
}
