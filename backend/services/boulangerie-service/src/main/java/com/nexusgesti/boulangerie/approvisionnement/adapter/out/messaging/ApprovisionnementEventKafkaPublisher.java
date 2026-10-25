package com.nexusgesti.boulangerie.approvisionnement.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementEventPublisher;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApprovisionnementEventKafkaPublisher implements ApprovisionnementEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ApprovisionnementEventKafkaPublisher.class);
    static final String TOPIC = "boulangerie.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ApprovisionnementEventKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publierCommandeValidee(CommandeAchat commande) {
        Map<String, Object> payload = base();
        payload.put("commandeId", commande.getIdCommande().toString());
        payload.put("numero", commande.getNumero());
        payload.put("fournisseurTiersCode", commande.getFournisseurTiersCode());
        payload.put("montantTTC", commande.getMontantTTC());
        payload.put("devise", commande.getDevise());
        payload.put("nbLignes", commande.getLignes().size());
        publier("commande.validee", commande.getIdCommande().toString(), payload);
    }

    @Override
    public void publierCommandeAnnulee(CommandeAchat commande, String motif) {
        Map<String, Object> payload = base();
        payload.put("commandeId", commande.getIdCommande().toString());
        payload.put("numero", commande.getNumero());
        payload.put("motif", motif);
        publier("commande.annulee", commande.getIdCommande().toString(), payload);
    }

    @Override
    public void publierReceptionAcceptee(Reception reception) {
        Map<String, Object> payload = base();
        payload.put("receptionId", reception.getIdReception().toString());
        payload.put("numero", reception.getNumero());
        payload.put("commandeId", reception.getCommandeId().toString());
        payload.put("commandeNumero", reception.getCommandeNumero());
        payload.put("statut", reception.getStatut().name());
        payload.put("nbLignes", reception.getLignes().size());
        publier("reception.acceptee", reception.getIdReception().toString(), payload);
    }

    @Override
    public void publierReceptionRefusee(Reception reception, String motif) {
        Map<String, Object> payload = base();
        payload.put("receptionId", reception.getIdReception().toString());
        payload.put("numero", reception.getNumero());
        payload.put("commandeId", reception.getCommandeId().toString());
        payload.put("commandeNumero", reception.getCommandeNumero());
        payload.put("motif", motif);
        publier("reception.refusee", reception.getIdReception().toString(), payload);
    }

    @Override
    public void publierMouvementStock(MouvementStockIntention intention) {
        Map<String, Object> payload = base();
        payload.put("type", intention.type().name());
        payload.put("articleCode", intention.articleCode());
        payload.put("lotNumero", intention.lotNumero());
        payload.put("quantite", intention.quantite());
        payload.put("prixMoyen", intention.prixAchat());
        payload.put("sourceId", intention.sourceId());
        payload.put("origine", "boulangerie-approvisionnement");
        publier("mouvement.stock", intention.sourceId(), payload);
    }

    @Override
    public void publierBesoinsSousSeuil(List<BesoinApprovisionnement> besoins) {
        Map<String, Object> payload = base();
        payload.put("nbBesoins", besoins.size());
        payload.put("articles", besoins.stream().map(BesoinApprovisionnement::articleCode).toList());
        payload.put("urgences", besoins.stream()
                .map(b -> b.articleCode() + ":" + b.urgence()).toList());
        publier("besoin.sous-seuil", besoins.isEmpty() ? null : besoins.get(0).articleCode(), payload);
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
