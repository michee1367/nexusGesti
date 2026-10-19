package com.nexusgesti.boulangerie.production.adapter.out.client;

import com.nexusgesti.boulangerie.production.domain.ProductionException;
import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.production.domain.port.out.RecetteProvider;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class ProduitRecetteClient implements RecetteProvider {

    private static final String INTERNAL_HEADER = "X-Internal-Service-Token";

    private final RestClient client;
    private final String internalToken;

    public ProduitRecetteClient(RestClient produitRestClient,
                                @Value("${nexusgesti.internal-service-token:dev-internal-token}") String internalToken) {
        this.client = produitRestClient;
        this.internalToken = internalToken;
    }

    @Override
    public List<BesoinMatiere> resoudreBesoins(String recetteCode, Integer versionRecette, BigDecimal quantite) {
        try {
            BesoinMatiere[] besoins = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/recettes/{code}/besoins")
                            .queryParamIfPresent("version", Optional.ofNullable(versionRecette))
                            .queryParam("quantite", quantite)
                            .build(recetteCode))
                    .header(INTERNAL_HEADER, internalToken)
                    .header(TenantContext.HEADER, TenantContext.require().asUuid().toString())
                    .retrieve()
                    .body(BesoinMatiere[].class);
            return besoins == null ? List.of() : List.of(besoins);
        } catch (RestClientResponseException e) {
            throw ProductionException.invalide(
                    "Recette indisponible : " + recetteCode + " (statut " + e.getStatusCode().value() + ")");
        }
    }
}
