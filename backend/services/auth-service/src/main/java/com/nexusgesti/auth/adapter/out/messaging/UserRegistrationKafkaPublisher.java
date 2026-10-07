package com.nexusgesti.auth.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.auth.domain.model.RegisteredUser;
import com.nexusgesti.auth.domain.port.in.AuthUseCase;
import com.nexusgesti.auth.domain.port.out.UserRegistrationEventPublisher;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRegistrationKafkaPublisher implements UserRegistrationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationKafkaPublisher.class);
    static final String TOPIC = "tenant.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UserRegistrationKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishUserRegistered(RegisteredUser user, AuthUseCase.RegisterCommand command) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", user.userId().toString());
            payload.put("email", user.email());
            payload.put("phone", command.phone());
            payload.put("firstName", command.firstName());
            payload.put("lastName", command.lastName());
            payload.put("countryCode", command.countryCode());
            payload.put("status", user.status());
            payload.put("timestamp", Instant.now().toString());
            TenantContext.get().ifPresent(tenant -> payload.put("tenantId", tenant.toString()));

            String message = objectMapper.writeValueAsString(Map.of(
                    "eventType", "user.created",
                    "payload", payload
            ));
            kafkaTemplate.send(TOPIC, user.userId().toString(), message);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish user.created event", e);
        }
    }
}
