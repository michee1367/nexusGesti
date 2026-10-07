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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Resultat critique : notification immediate au prescripteur avant validation
 * biologique. declencheNotificationCritique() ne depend d'aucune comparaison de
 * valeur, un resultat critique non declare est une anomalie de processus.
 */
@Component
public class HopitalNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(HopitalNotificationConsumer.class);

    private final NotificationUseCase notificationUseCase;
    private final ObjectMapper objectMapper;

    public HopitalNotificationConsumer(NotificationUseCase notificationUseCase,
                                       ObjectMapper objectMapper) {
        this.notificationUseCase = notificationUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "hopital.events", groupId = "notification-service")
    public void onHopitalEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            if (!"resultat.critique".equals(root.path("eventType").asText())) {
                return;
            }
            JsonNode payload = root.path("payload");
            TenantContext.setIfPresent(
                    payload.hasNonNull("tenantId") ? payload.get("tenantId").asText() : null);

            UUID prescripteurId = UUID.fromString(payload.path("prescripteurId").asText());
            String email = payload.path("email").asText();

            Map<String, String> variables = new HashMap<>();
            variables.put("patientId", payload.path("patientId").asText());
            variables.put("codeExamen", payload.path("codeExamen").asText());
            variables.put("libelle", payload.path("libelle").asText());
            if (payload.hasNonNull("valeur")) {
                variables.put("valeur", payload.path("valeur").asText());
            }
            if (payload.hasNonNull("unite")) {
                variables.put("unite", payload.path("unite").asText());
            }

            notificationUseCase.send(new NotificationUseCase.SendNotificationCommand(
                    prescripteurId,
                    Notification.Channel.EMAIL,
                    "hopital.resultat.critique",
                    email,
                    "Resultat critique a valider",
                    variables,
                    Notification.Priority.CRITICAL
            ));

            log.warn("Critical result notification sent to prescriber {} for patient {}",
                    prescripteurId, variables.get("patientId"));
        } catch (Exception e) {
            log.error("Failed to process hopital event: {}", message, e);
        } finally {
            TenantContext.clear();
        }
    }
}
