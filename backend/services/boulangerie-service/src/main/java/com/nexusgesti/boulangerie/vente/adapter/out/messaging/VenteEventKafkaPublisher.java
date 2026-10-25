package com.nexusgesti.boulangerie.vente.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;
import com.nexusgesti.boulangerie.vente.domain.port.out.VenteEventPublisher;
import com.nexusgesti.common.tenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class VenteEventKafkaPublisher implements VenteEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(VenteEventKafkaPublisher.class);
    static final String TOPIC = "boulangerie.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public VenteEventKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publierVenteEncaissee(Vente vente) {
        Map<String, Object> payload = base();
        payload.put("venteId", vente.getIdVente().toString());
        payload.put("numeroTicket", vente.getNumeroTicket());
        payload.put("sessionId", vente.getSessionId().toString());
        payload.put("clientCode", vente.getClientCode());
        payload.put("montantTTC", vente.getMontantTTC());
        payload.put("modePaiement", vente.getModePaiement().name());
        payload.put("canal", vente.getCanal().name());
        payload.put("nbLignes", vente.getLignes().size());
        publier("vente.encaissee", vente.getIdVente().toString(), payload);
    }

    @Override
    public void publierVenteAnnulee(Vente vente, String motif) {
        Map<String, Object> payload = base();
        payload.put("venteId", vente.getIdVente().toString());
        payload.put("numeroTicket", vente.getNumeroTicket());
        payload.put("motif", motif);
        publier("vente.annulee", vente.getIdVente().toString(), payload);
    }

    @Override
    public void publierSessionCloturee(SessionCaisse session) {
        Map<String, Object> payload = base();
        payload.put("sessionId", session.getIdSession().toString());
        payload.put("date", session.getDate().toString());
        payload.put("totalEncaisse", session.getTotalEncaisse());
        payload.put("ecart", session.getEcart());
        publier("caisse.cloturee", session.getIdSession().toString(), payload);
    }

    @Override
    public void publierCommandeCreee(CommandeClient commande) {
        Map<String, Object> payload = base();
        payload.put("commandeId", commande.getIdCommande().toString());
        payload.put("numero", commande.getNumero());
        payload.put("clientCode", commande.getClientCode());
        payload.put("montantTTC", commande.getMontantTTC());
        payload.put("nbLignes", commande.getLignes().size());
        publier("commande.client.creee", commande.getIdCommande().toString(), payload);
    }

    @Override
    public void publierCommandeConfirmee(CommandeClient commande) {
        Map<String, Object> payload = base();
        payload.put("commandeId", commande.getIdCommande().toString());
        payload.put("numero", commande.getNumero());
        payload.put("clientCode", commande.getClientCode());
        publier("commande.client.confirmee", commande.getIdCommande().toString(), payload);
    }

    @Override
    public void publierCommandeAnnulee(CommandeClient commande, String motif) {
        Map<String, Object> payload = base();
        payload.put("commandeId", commande.getIdCommande().toString());
        payload.put("numero", commande.getNumero());
        payload.put("motif", motif);
        publier("commande.client.annulee", commande.getIdCommande().toString(), payload);
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
        payload.put("origine", "boulangerie-vente");
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
