package com.nexusgesti.audit.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.audit.domain.model.AuditEntry;
import com.nexusgesti.audit.domain.port.in.AuditUseCase;
import com.nexusgesti.common.tenancy.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuditKafkaConsumerTest {

    private final AuditUseCase auditUseCase = mock(AuditUseCase.class);
    private final AuditKafkaConsumer consumer = new AuditKafkaConsumer(auditUseCase, new ObjectMapper());

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void payloadSansActionDefautSurEventTypeEtPersiste() {
        String message = """
                {"eventType":"eleve.inscrit","payload":{"tenantId":"11111111-2222-3333-4444-555555555556",
                "eleveId":"b9c247d6-65e9-4350-8a6d-26e55f2afbbb",
                "entityId":"b9c247d6-65e9-4350-8a6d-26e55f2afbbb"}}
                """;

        consumer.consume(message, "ped.events");

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditUseCase).logEvent(captor.capture());
        AuditEntry entry = captor.getValue();

        assertThat(entry.getEventType()).isEqualTo("eleve.inscrit");
        assertThat(entry.getAction()).isEqualTo("eleve.inscrit");
        assertThat(entry.getServiceName()).isEqualTo("ped-service");
        assertThat(entry.getEntityId()).isEqualTo("b9c247d6-65e9-4350-8a6d-26e55f2afbbb");
        assertThat(entry.getMetadata()).containsEntry("topic", "ped.events");
    }

    @Test
    void actionExpliciteRacineEstPrioritaire() {
        String message = """
                {"eventType":"eleve.inscrit","action":"INSCRIRE","entityId":"abc",
                "payload":{"tenantId":"11111111-2222-3333-4444-555555555556"}}
                """;

        consumer.consume(message, "ped.events");

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditUseCase).logEvent(captor.capture());
        AuditEntry entry = captor.getValue();

        assertThat(entry.getAction()).isEqualTo("INSCRIRE");
        assertThat(entry.getEntityId()).isEqualTo("abc");
    }
}
