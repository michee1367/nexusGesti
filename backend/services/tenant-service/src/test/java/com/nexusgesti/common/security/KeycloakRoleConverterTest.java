package com.nexusgesti.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeycloakRoleConverterTest {

    private Jwt jwtConRoles(String... roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("subject")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("realm_access", Map.of("roles", List.of(roles)))
                .build();
    }

    @Test
    void traduitLesRolesDuRealmEnAuthorities() {
        KeycloakRoleConverter converter = new KeycloakRoleConverter();
        List<String> authorities = converter.convert(jwtConRoles("admin", "customer")).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(List.of("ROLE_ADMIN", "ROLE_CUSTOMER"), authorities);
    }

    @Test
    void sansRealmAccessAucuneAutorite() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("subject")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        assertTrue(new KeycloakRoleConverter().convert(jwt).isEmpty());
    }
}