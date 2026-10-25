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

/**
 * Controles de facturation : seuls les profiles ALERTE et INFORMATIONNEL
 * notifient. Un controle BLOQUANT arrete la facturation en amont, il n'a pas
 * de destinataire a prevenir.
 */
@Component
public class FinanceNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(FinanceNotificationConsumer.class);

    private static final String EVENT_TYPE = "controle.facturation";

    private final NotificationUseCase notificationUseCase;
    private final ObjectMapper objectMapper;

    public FinanceNotificationConsumer(NotificationUseCase notificationUseCase,
                                       ObjectMapper objectMapper) {
        this.notificationUseCase = notificationUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "fin.events", groupId = "notification-service")
    public void onFinanceEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            if (!EVENT_TYPE.equals(root.path("eventType").asText())) {
                return;
            }
            JsonNode payload = root.path("payload");
            TenantContext.setIfPresent(
                    payload.hasNonNull("tenantId") ? payload.get("tenantId").asText() : null);

            String profil = payload.path("profil").asText();
            if ("BLOQUANT".equals(profil)) {
                return;
            }

            UUID userId = UUID.fromString(payload.path("userId").asText());
            String email = payload.path("email").asText();

            notificationUseCase.send(new NotificationUseCase.SendNotificationCommand(
                    userId,
                    Notification.Channel.EMAIL,
                    "fin.controle.facturation." + profil.toLowerCase(),
                    email,
                    "Controle de facturation : " + profil,
                    Map.of(
                            "profil", profil,
                            "libelle", payload.path("libelle").asText(),
                            "reference", payload.path("reference").asText()
                    ),
                    "ALERTE".equals(profil) ? Notification.Priority.HIGH : Notification.Priority.NORMAL
            ));

            log.info("Billing control {} notification sent to user {}", profil, userId);
        } catch (Exception e) {
            log.error("Failed to process finance event: {}", message, e);
        } finally {
            TenantContext.clear();
        }
    }
}
