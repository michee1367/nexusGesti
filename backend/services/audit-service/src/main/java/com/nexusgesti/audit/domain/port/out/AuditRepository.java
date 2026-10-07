package com.nexusgesti.audit.domain.port.out;

import com.nexusgesti.audit.domain.model.AuditEntry;
import com.nexusgesti.audit.domain.model.AuditSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditRepository {

    AuditEntry save(AuditEntry entry);

    List<AuditEntry> search(AuditSearchCriteria criteria);

    List<AuditEntry> findByEntityTypeAndEntityId(String entityType, String entityId);

    Optional<AuditEntry> findById(UUID id);
}
