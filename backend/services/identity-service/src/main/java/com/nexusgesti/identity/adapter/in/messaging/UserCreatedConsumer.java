package com.nexusgesti.identity.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedConsumer.class);

    private final UtilisateurUseCase utilisateurUseCase;
    private final ObjectMapper objectMapper;

    public UserCreatedConsumer(UtilisateurUseCase utilisateurUseCase, ObjectMapper objectMapper) {
        this.utilisateurUseCase = utilisateurUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "tenant.events", groupId = "identity-service")
    public void consume(@Payload String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            if (!"user.created".equals(text(node, "eventType"))) {
                return;
            }
            JsonNode payload = node.has("payload") && node.get("payload").isObject()
                    ? node.get("payload") : node;
            String userId = text(payload, "userId");
            String tenantId = text(payload, "tenantId");
            if (userId == null || tenantId == null) {
                log.warn("user.created sans userId ou tenantId : ligne identite non creee");
                return;
            }

            TenantContext.setIfPresent(tenantId);

            utilisateurUseCase.enregistrerDepuisEvenement(new UtilisateurUseCase.EnregistrerUtilisateurCommand(
                    UUID.fromString(userId),
                    text(payload, "email"),
                    text(payload, "lastName"),
                    text(payload, "firstName")));
            log.info("Ligne identite creee depuis user.created pour {}", userId);
        } catch (Exception e) {
            log.error("Failed to process tenant.events user.created: {}", e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    private String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
