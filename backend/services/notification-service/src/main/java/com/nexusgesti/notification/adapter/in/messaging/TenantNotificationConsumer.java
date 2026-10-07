package com.nexusgesti.notification.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.notification.domain.model.Notification;
import com.nexusgesti.notification.domain.port.in.NotificationUseCase;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TenantNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(TenantNotificationConsumer.class);

    private final NotificationUseCase notificationUseCase;
    private final ObjectMapper objectMapper;

    public TenantNotificationConsumer(NotificationUseCase notificationUseCase,
                                      ObjectMapper objectMapper) {
        this.notificationUseCase = notificationUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "tenant.events", groupId = "notification-service")
    public void onTenantEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            if (!"user.created".equals(root.path("eventType").asText())) {
                return;
            }
            JsonNode payload = root.path("payload");
            TenantContext.setIfPresent(
                    payload.hasNonNull("tenantId") ? payload.get("tenantId").asText() : null);

            UUID userId = UUID.fromString(payload.path("userId").asText());
            String email = payload.path("email").asText();
            String firstName = payload.path("firstName").asText();

            notificationUseCase.send(new NotificationUseCase.SendNotificationCommand(
                    userId,
                    Notification.Channel.EMAIL,
                    "tenant.user.created",
                    email,
                    "Votre compte NexusGesti a ete cree",
                    Map.of("firstName", firstName),
                    Notification.Priority.NORMAL
            ));

            log.info("Account creation notification sent to user {}", userId);
        } catch (Exception e) {
            log.error("Failed to process tenant event: {}", message, e);
        } finally {
            TenantContext.clear();
        }
    }
}
