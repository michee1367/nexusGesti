package com.nexusgesti.identity.adapter.out.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexusgesti.identity.config.KeycloakProperties;
import com.nexusgesti.identity.domain.IdentityException;
import com.nexusgesti.identity.domain.port.out.IdentityProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component("identityKeycloakIdentityProviderAdapter")
public class KeycloakIdentityProviderAdapter implements IdentityProviderPort {

    private static final Logger log = LoggerFactory.getLogger(KeycloakIdentityProviderAdapter.class);

    private final RestClient client;
    private final KeycloakProperties properties;

    public KeycloakIdentityProviderAdapter(KeycloakProperties properties) {
        this.properties = properties;
        this.client = RestClient.builder().baseUrl(properties.serverUrl()).build();
    }

    @Override
    public UUID createUser(CreateUser command) {
        String token = adminToken();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", command.username());
        body.put("email", command.email());
        body.put("firstName", command.firstName());
        body.put("lastName", command.lastName());
        body.put("enabled", command.enabled());
        body.put("emailVerified", true);
        if (command.attributes() != null && !command.attributes().isEmpty()) {
            body.put("attributes", toAttributeLists(command.attributes()));
        }
        if (command.password() != null && !command.password().isBlank()) {
            body.put("credentials", List.of(Map.of(
                    "type", "password",
                    "value", command.password(),
                    "temporary", command.temporaryPassword()
            )));
        }
        if (command.temporaryPassword()) {
            body.put("requiredActions", List.of("UPDATE_PASSWORD"));
        }

        try {
            var response = client.post()
                    .uri(properties.adminUsersEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
            if (location == null) {
                throw IdentityException.keycloakIndisponible("Keycloak n'a pas retourne d'identifiant utilisateur");
            }
            return UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
        } catch (RestClientResponseException e) {
            throw map(e, "Echec creation utilisateur Keycloak");
        }
    }

    @Override
    public void updateUser(UUID keycloakUserId, UpdateUser command) {
        String token = adminToken();
        Map<String, Object> current = requireUser(keycloakUserId, token);
        if (command.email() != null) current.put("email", command.email());
        if (command.firstName() != null) current.put("firstName", command.firstName());
        if (command.lastName() != null) current.put("lastName", command.lastName());
        if (command.attributes() != null && !command.attributes().isEmpty()) {
            current.put("attributes", mergeAttributes(current.get("attributes"), command.attributes()));
        }
        putUser(keycloakUserId, token, current);
    }

    @Override
    public void deleteUser(UUID keycloakUserId) {
        String token = adminToken();
        try {
            client.delete()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() != 404) {
                throw map(e, "Echec suppression utilisateur Keycloak");
            }
        }
    }

    @Override
    public void setEnabled(UUID keycloakUserId, boolean enabled) {
        String token = adminToken();
        Map<String, Object> current = requireUser(keycloakUserId, token);
        current.put("enabled", enabled);
        putUser(keycloakUserId, token, current);
    }

    @Override
    public void setAttributes(UUID keycloakUserId, Map<String, String> attributes) {
        String token = adminToken();
        Map<String, Object> current = requireUser(keycloakUserId, token);
        current.put("attributes", toAttributeLists(attributes));
        putUser(keycloakUserId, token, current);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributes(UUID keycloakUserId) {
        String token = adminToken();
        Map<String, Object> user = requireUser(keycloakUserId, token);
        Object raw = user.get("attributes");
        Map<String, String> result = new LinkedHashMap<>();
        if (raw instanceof Map<?, ?> map) {
            map.forEach((k, v) -> {
                if (v instanceof List<?> list && !list.isEmpty()) {
                    result.put(String.valueOf(k), String.valueOf(list.getFirst()));
                } else if (v != null) {
                    result.put(String.valueOf(k), String.valueOf(v));
                }
            });
        }
        return result;
    }

    @Override
    public void assignRealmRole(UUID keycloakUserId, String roleName) {
        String token = adminToken();
        KeycloakRoleRepresentation role = findRoleRepresentation(roleName, token);
        if (role == null) {
            throw IdentityException.conflit("ROLE_INCONNU", "Role Keycloak inexistant : " + roleName);
        }
        try {
            client.post()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()) + "/role-mappings/realm")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(role))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec attribution du role " + roleName);
        }
    }

    @Override
    public void removeRealmRole(UUID keycloakUserId, String roleName) {
        String token = adminToken();
        KeycloakRoleRepresentation role = findRoleRepresentation(roleName, token);
        if (role == null) {
            return;
        }
        try {
            client.method(org.springframework.http.HttpMethod.DELETE)
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()) + "/role-mappings/realm")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(role))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec retrait du role " + roleName);
        }
    }

    @Override
    public List<String> listUserRealmRoles(UUID keycloakUserId) {
        String token = adminToken();
        try {
            List<KeycloakRoleRepresentation> roles = client.get()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()) + "/role-mappings/realm")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (roles == null) {
                return List.of();
            }
            return roles.stream().map(KeycloakRoleRepresentation::name).toList();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec lecture des roles de l'utilisateur");
        }
    }

    @Override
    public void resetPassword(UUID keycloakUserId, String password, boolean temporary) {
        String token = adminToken();
        try {
            client.put()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()) + "/reset-password")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("type", "password", "value", password, "temporary", temporary))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec reinitialisation du mot de passe");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void requirePasswordChange(UUID keycloakUserId, boolean required) {
        String token = adminToken();
        Map<String, Object> current = requireUser(keycloakUserId, token);
        List<String> actions = current.get("requiredActions") instanceof List<?> list
                ? new java.util.ArrayList<>(list.stream().map(String::valueOf).toList())
                : new java.util.ArrayList<>();
        actions.remove("UPDATE_PASSWORD");
        if (required) {
            actions.add("UPDATE_PASSWORD");
        }
        current.put("requiredActions", actions);
        putUser(keycloakUserId, token, current);
    }

    @Override
    public Optional<UUID> findUserIdByEmail(String email) {
        String token = adminToken();
        try {
            List<KeycloakUserRepresentation> users = client.get()
                    .uri(properties.adminUsersEndpoint() + "?email={email}&exact=true", email)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (users == null || users.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(UUID.fromString(users.getFirst().id()));
        } catch (RestClientResponseException e) {
            throw map(e, "Echec recherche utilisateur Keycloak");
        }
    }

    @Override
    public List<String> searchUserIds(String query) {
        String token = adminToken();
        String search = query == null ? "" : query;
        try {
            List<KeycloakUserRepresentation> users = client.get()
                    .uri(properties.adminUsersEndpoint() + "?search={q}&max=50", search)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (users == null) {
                return List.of();
            }
            return users.stream().map(KeycloakUserRepresentation::id).toList();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec recherche utilisateurs Keycloak");
        }
    }

    @Override
    public List<RealmRole> listRealmRoles() {
        String token = adminToken();
        try {
            List<KeycloakRoleRepresentation> roles = client.get()
                    .uri(properties.adminRolesEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (roles == null) {
                return List.of();
            }
            return roles.stream().map(this::toRealmRole).toList();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec lecture des roles Keycloak");
        }
    }

    @Override
    public Optional<RealmRole> findRealmRole(String name) {
        String token = adminToken();
        KeycloakRoleRepresentation role = findRoleRepresentation(name, token);
        return Optional.ofNullable(role).map(this::toRealmRole);
    }

    @Override
    public void createRealmRole(RealmRole role) {
        String token = adminToken();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", role.name());
        if (role.description() != null) body.put("description", role.description());
        if (role.attributes() != null && !role.attributes().isEmpty()) {
            body.put("attributes", toAttributeLists(role.attributes()));
        }
        try {
            client.post()
                    .uri(properties.adminRolesEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec creation du role " + role.name());
        }
    }

    @Override
    public void deleteRealmRole(String name) {
        String token = adminToken();
        try {
            client.delete()
                    .uri(properties.adminRoleEndpoint(name))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() != 404) {
                throw map(e, "Echec suppression du role " + name);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Instant lastLogin(UUID keycloakUserId) {
        String token = adminToken();
        try {
            List<Map<String, Object>> events = client.get()
                    .uri(properties.serverUrl() + "/admin/realms/" + properties.realm()
                            + "/events?user={id}&type=LOGIN&max=1", keycloakUserId.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (events == null || events.isEmpty()) {
                return null;
            }
            Object time = events.getFirst().get("time");
            if (time instanceof Number n) {
                return Instant.ofEpochMilli(n.longValue());
            }
            return null;
        } catch (Exception e) {
            log.debug("lastLogin indisponible pour {}: {}", keycloakUserId, e.getMessage());
            return null;
        }
    }

    private KeycloakRoleRepresentation findRoleRepresentation(String roleName, String token) {
        try {
            return client.get()
                    .uri(properties.adminRoleEndpoint(roleName))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(KeycloakRoleRepresentation.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw map(e, "Echec lecture du role " + roleName);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> requireUser(UUID keycloakUserId, String token) {
        try {
            Map<String, Object> user = client.get()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (user == null) {
                throw IdentityException.notFound("Utilisateur Keycloak", keycloakUserId);
            }
            return new LinkedHashMap<>(user);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw IdentityException.notFound("Utilisateur Keycloak", keycloakUserId);
            }
            throw map(e, "Echec lecture utilisateur Keycloak");
        }
    }

    private void putUser(UUID keycloakUserId, String token, Map<String, Object> body) {
        try {
            client.put()
                    .uri(properties.adminUserEndpoint(keycloakUserId.toString()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw map(e, "Echec mise a jour utilisateur Keycloak");
        }
    }

    private String adminToken() {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "client_credentials");
            form.add("client_id", properties.adminClientId());
            form.add("client_secret", properties.adminClientSecret());
            TokenResponse response = client.post()
                    .uri(properties.tokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw IdentityException.keycloakIndisponible("Keycloak admin indisponible");
            }
            return response.accessToken();
        } catch (RestClientResponseException e) {
            throw IdentityException.keycloakIndisponible("Authentification admin Keycloak echouee ("
                    + e.getStatusCode().value() + ")");
        }
    }

    private RealmRole toRealmRole(KeycloakRoleRepresentation role) {
        Map<String, String> attributes = new LinkedHashMap<>();
        if (role.attributes() != null) {
            role.attributes().forEach((k, v) -> {
                if (v instanceof List<?> list && !list.isEmpty()) {
                    attributes.put(k, String.valueOf(list.getFirst()));
                } else if (v != null) {
                    attributes.put(k, String.valueOf(v));
                }
            });
        }
        return new RealmRole(role.name(), role.description(), attributes);
    }

    private Map<String, List<String>> toAttributeLists(Map<String, String> attributes) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (attributes != null) {
            attributes.forEach((k, v) -> {
                if (v != null) {
                    result.put(k, List.of(v));
                }
            });
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> mergeAttributes(Object existing, Map<String, String> updates) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (existing instanceof Map<?, ?> map) {
            ((Map<String, Object>) map).forEach((k, v) -> {
                if (v instanceof List<?> list) {
                    result.put(k, list.stream().map(String::valueOf).toList());
                } else if (v != null) {
                    result.put(k, List.of(String.valueOf(v)));
                }
            });
        }
        updates.forEach((k, v) -> {
            if (v == null) {
                result.remove(k);
            } else {
                result.put(k, List.of(v));
            }
        });
        return result;
    }

    private IdentityException map(RestClientResponseException e, String fallback) {
        log.warn("{} : {} {}", fallback, e.getStatusCode().value(), e.getResponseBodyAsString());
        if (e.getStatusCode().value() == 409) {
            return IdentityException.conflit("KEYCLOAK_CONFLIT", fallback);
        }
        return IdentityException.keycloakIndisponible(fallback + " (" + e.getStatusCode().value() + ")");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KeycloakUserRepresentation(
            String id,
            String username,
            String email,
            String firstName,
            String lastName,
            Boolean enabled,
            Boolean emailVerified,
            Map<String, Object> attributes
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KeycloakRoleRepresentation(
            String id,
            String name,
            String description,
            Map<String, List<String>> attributes
    ) {}
}
