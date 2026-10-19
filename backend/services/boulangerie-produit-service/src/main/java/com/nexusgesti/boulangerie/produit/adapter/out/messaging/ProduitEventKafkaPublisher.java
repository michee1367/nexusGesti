package com.nexusgesti.boulangerie.produit.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.produit.domain.model.FicheRecette;
import com.nexusgesti.boulangerie.produit.domain.model.Produit;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitEventPublisher;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProduitEventKafkaPublisher implements ProduitEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProduitEventKafkaPublisher.class);
    static final String TOPIC = "boulangerie.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProduitEventKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publierProduitCree(Produit produit) {
        Map<String, Object> payload = base();
        payload.put("produitId", produit.getIdProduit().toString());
        payload.put("code", produit.getCode());
        payload.put("designation", produit.getDesignation());
        payload.put("gammeCode", produit.getGammeCode());
        payload.put("categorieCode", produit.getCategorieCode());
        payload.put("articleCode", produit.getArticleCode());
        payload.put("uniteVente", produit.getUniteVente() == null ? null : produit.getUniteVente().name());
        payload.put("prixVenteHT", produit.getPrixVenteHT());
        payload.put("tauxTva", produit.getTauxTva());
        publier("produit.cree", produit.getIdProduit().toString(), payload);
    }

    @Override
    public void publierRecetteActivee(FicheRecette recette) {
        publierRecette("recette.activee", recette);
    }

    @Override
    public void publierRecetteFigee(FicheRecette recette) {
        publierRecette("recette.figee", recette);
    }

    private void publierRecette(String eventType, FicheRecette recette) {
        Map<String, Object> payload = base();
        payload.put("recetteId", recette.getIdRecette().toString());
        payload.put("code", recette.getCode());
        payload.put("produitCode", recette.getProduitCode());
        payload.put("version", recette.getVersion());
        publier(eventType, recette.getIdRecette().toString(), payload);
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
