package com.nexusgesti.boulangerie.production.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient produitRestClient(
            @Value("${nexusgesti.boulangerie.produit-url:http://localhost:8141}") String produitUrl) {
        return RestClient.builder().baseUrl(produitUrl).build();
    }
}
