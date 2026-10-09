package com.nexusgesti.audit.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.audit.domain.model.AuditEntry;
import com.nexusgesti.audit.domain.port.in.AuditUseCase;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditKafkaConsumer.class);

    private final AuditUseCase auditUseCase;
    private final ObjectMapper objectMapper;

    public AuditKafkaConsumer(AuditUseCase auditUseCase, ObjectMapper objectMapper) {
        this.auditUseCase = auditUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"tenant.events", "ped.events", "hopital.events", "fin.events"},
                   groupId = "audit-service")
    public void consume(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            JsonNode node = objectMapper.readTree(message);
            applyTenant(node);
            JsonNode payload = node.has("payload") && node.get("payload").isObject()
                    ? node.get("payload") : node;

            AuditEntry entry = new AuditEntry();
            entry.setId(UUID.randomUUID());
            entry.setServiceName(extractServiceName(topic));
            String eventType = firstText(node, payload, "eventType");
            entry.setEventType(eventType);
            entry.setUserId(firstUuid(node, payload, "userId"));
            entry.setEntityType(firstText(node, payload, "entityType"));
            entry.setEntityId(firstText(node, payload, "entityId"));
            String action = firstText(node, payload, "action");
            entry.setAction(action != null ? action : (eventType != null ? eventType : "UNKNOWN"));
            entry.setOldValue(firstJson(node, payload, "oldValue"));
            entry.setNewValue(firstJson(node, payload, "newValue"));
            entry.setIpAddress(firstText(node, payload, "ipAddress"));
            entry.setTimestamp(Instant.now());

            Map<String, String> metadata = new HashMap<>();
            metadata.put("topic", topic);
            JsonNode metaNode = node.has("metadata") && node.get("metadata").isObject()
                    ? node.get("metadata")
                    : (payload.has("metadata") && payload.get("metadata").isObject() ? payload.get("metadata") : null);
            if (metaNode != null) {
                metaNode.fields().forEachRemaining(
                        field -> metadata.put(field.getKey(), field.getValue().asText()));
            }
            entry.setMetadata(metadata);

            auditUseCase.logEvent(entry);
            log.debug("Audit entry saved for topic={}, eventType={}", topic, entry.getEventType());

        } catch (Exception e) {
            log.error("Failed to process audit event from topic={}: {}", topic, e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    private void applyTenant(JsonNode node) {
        JsonNode payload = node.has("payload") ? node.get("payload") : node;
        String tenantId = getTextOrNull(payload, "tenantId");
        if (tenantId == null) {
            tenantId = getTextOrNull(node, "tenantId");
        }
        TenantContext.setIfPresent(tenantId);
    }

    private String extractServiceName(String topic) {
        return topic.replace(".events", "-service");
    }

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private String firstText(JsonNode root, JsonNode payload, String field) {
        String value = getTextOrNull(root, field);
        return value != null ? value : getTextOrNull(payload, field);
    }

    private UUID firstUuid(JsonNode root, JsonNode payload, String field) {
        String text = firstText(root, payload, field);
        if (text == null) return null;
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String firstJson(JsonNode root, JsonNode payload, String field) {
        if (root.has(field) && !root.get(field).isNull()) return root.get(field).toString();
        if (payload.has(field) && !payload.get(field).isNull()) return payload.get(field).toString();
        return null;
    }
}
