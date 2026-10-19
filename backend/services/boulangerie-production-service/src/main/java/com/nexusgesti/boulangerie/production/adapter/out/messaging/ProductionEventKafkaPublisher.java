package com.nexusgesti.boulangerie.production.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionEventPublisher;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductionEventKafkaPublisher implements ProductionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductionEventKafkaPublisher.class);
    static final String TOPIC = "boulangerie.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProductionEventKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                         ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publierPlanValide(PlanProduction plan) {
        Map<String, Object> payload = base();
        payload.put("planId", plan.getIdPlan().toString());
        payload.put("date", plan.getDate() == null ? null : plan.getDate().toString());
        payload.put("atelierCode", plan.getAtelierCode());
        payload.put("nbLignes", plan.getLignes().size());
        publier("plan.valide", plan.getIdPlan().toString(), payload);
    }

    @Override
    public void publierFourneeValidee(Fournee fournee) {
        Map<String, Object> payload = base();
        payload.put("fourneeId", fournee.getIdFournee().toString());
        payload.put("numero", fournee.getNumero());
        payload.put("recetteCode", fournee.getRecetteCode());
        payload.put("versionRecette", fournee.getVersionRecette());
        payload.put("produitCode", fournee.getProduitCode());
        payload.put("quantitePrevue", fournee.getQuantitePrevue());
        payload.put("quantiteRealisee", fournee.getQuantiteRealisee());
        publier("fournee.validee", fournee.getIdFournee().toString(), payload);
    }

    @Override
    public void publierMouvementStock(MouvementStockIntention intention) {
        Map<String, Object> payload = base();
        payload.put("type", intention.type().name());
        payload.put("articleCode", intention.articleCode());
        payload.put("lotNumero", intention.lotNumero());
        payload.put("quantite", intention.quantite());
        payload.put("prixMoyen", intention.prixMoyen());
        payload.put("sourceId", intention.sourceId());
        payload.put("origine", "boulangerie-production");
        publier("mouvement.stock", intention.sourceId(), payload);
    }

    private Map<String, Object> base() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now().toString());
        TenantContext.get().ifPresent(tenant -> payload.put("tenantId", tenant.toString()));
        return payload;
    }

    private void publier(String eventType, String key, Map<String, Object> payload) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                    "eventType", eventType,
                    "payload", payload
            ));
            kafkaTemplate.send(TOPIC, key, message);
        } catch (JsonProcessingException e) {
            log.error("Echec publication evenement {}", eventType, e);
        }
    }
}
