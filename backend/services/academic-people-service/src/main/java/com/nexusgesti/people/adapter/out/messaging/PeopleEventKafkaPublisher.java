package com.nexusgesti.people.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.people.domain.model.Eleve;
import com.nexusgesti.people.domain.model.Inscription;
import com.nexusgesti.people.domain.port.out.InscriptionEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class PeopleEventKafkaPublisher implements InscriptionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PeopleEventKafkaPublisher.class);
    static final String TOPIC = "ped.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PeopleEventKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publierEleveInscrit(Eleve eleve, Inscription inscription) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("eleveId", eleve.getIdEleve().toString());
            payload.put("matricule", eleve.getMatricule());
            payload.put("inscriptionId", inscription.getIdInscription().toString());
            payload.put("anneeId", inscription.getAnneeId() == null ? null : inscription.getAnneeId().toString());
            payload.put("classeId", inscription.getClasseId() == null ? null : inscription.getClasseId().toString());
            payload.put("timestamp", Instant.now().toString());
            TenantContext.get().ifPresent(tenant -> payload.put("tenantId", tenant.toString()));

            String message = objectMapper.writeValueAsString(Map.of(
                    "eventType", "eleve.inscrit",
                    "payload", payload
            ));
            kafkaTemplate.send(TOPIC, eleve.getIdEleve().toString(), message);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish eleve.inscrit event", e);
        }
    }
}
