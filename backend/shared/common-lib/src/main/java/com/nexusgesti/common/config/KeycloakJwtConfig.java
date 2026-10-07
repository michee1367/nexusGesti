package com.nexusgesti.common.config;

import com.nexusgesti.common.security.KeycloakRoleConverter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Convertisseur JWT partage, disponible quel que soit le drapeau
 * nexusgesti.security.oauth2.enabled : les services qui declarent leur propre
 * SecurityFilterChain (audit, reporting) doivent brancher ce bean sur
 * oauth2ResourceServer(), sinon ils n exposent que SCOPE_* et hasRole() repond 403.
 */
@Configuration
public class KeycloakJwtConfig {

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}
