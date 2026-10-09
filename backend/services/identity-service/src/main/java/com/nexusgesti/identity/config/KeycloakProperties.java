package com.nexusgesti.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexusgesti.keycloak")
public record KeycloakProperties(
        String serverUrl,
        String realm,
        String clientId,
        String adminClientId,
        String adminClientSecret
) {
    public String tokenEndpoint() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String adminUsersEndpoint() {
        return serverUrl + "/admin/realms/" + realm + "/users";
    }

    public String adminUserEndpoint(String userId) {
        return adminUsersEndpoint() + "/" + userId;
    }

    public String adminRolesEndpoint() {
        return serverUrl + "/admin/realms/" + realm + "/roles";
    }

    public String adminRoleEndpoint(String roleName) {
        return adminRolesEndpoint() + "/" + roleName;
    }
}
