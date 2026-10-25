package com.nexusgesti.tenant.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.Etablissement;
import com.nexusgesti.tenant.domain.model.ProfilEtablissement;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("tenantUserCreatedConsumer")
public class UserCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedConsumer.class);

    private final EtablissementUseCase etablissementUseCase;
    private final ObjectMapper objectMapper;

    public UserCreatedConsumer(EtablissementUseCase etablissementUseCase, ObjectMapper objectMapper) {
        this.etablissementUseCase = etablissementUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "tenant.events", groupId = "tenant-service")
    public void consume(@Payload String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            if (!"user.created".equals(text(node, "eventType"))) {
                return;
            }
            JsonNode payload = node.has("payload") && node.get("payload").isObject()
                    ? node.get("payload") : node;
            String tenantId = text(payload, "tenantId");
            if (tenantId == null || tenantId.isBlank()) {
                log.warn("user.created sans tenantId : etablissement non cree");
                return;
            }
            JsonNode etab = payload.get("etablissement");
            if (etab == null || !etab.isObject()) {
                log.warn("user.created sans etablissement pour tenant {}", tenantId);
                return;
            }

            TenantContext.setIfPresent(tenantId);

            Etablissement etablissement = new Etablissement();
            etablissement.setDenomination(text(etab, "denomination"));
            etablissement.setCode(text(etab, "code"));
            String profil = text(etab, "profil");
            etablissement.setProfil(profil == null ? null : ProfilEtablissement.valueOf(profil));

            etablissementUseCase.creer(UUID.fromString(tenantId), etablissement);
            log.info("Etablissement {} cree depuis user.created", tenantId);
        } catch (TenantException e) {
            if ("TENANT_ALREADY_EXISTS".equals(e.getCode())) {
                log.debug("Etablissement deja cree : evenement rejoue, ignore");
            } else {
                log.error("Echec creation etablissement : {}", e.getMessage(), e);
            }
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
