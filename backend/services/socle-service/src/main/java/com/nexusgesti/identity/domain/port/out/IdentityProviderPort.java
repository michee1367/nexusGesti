package com.nexusgesti.identity.domain.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de sortie vers Keycloak : source de verite pour l'identite native
 * (username, email, nom, prenom, enabled), les preferences (attributes) et
 * les roles (realm roles).
 */
public interface IdentityProviderPort {

    UUID createUser(CreateUser command);

    void updateUser(UUID keycloakUserId, UpdateUser command);

    void deleteUser(UUID keycloakUserId);

    void setEnabled(UUID keycloakUserId, boolean enabled);

    void setAttributes(UUID keycloakUserId, Map<String, String> attributes);

    Map<String, String> getAttributes(UUID keycloakUserId);

    void assignRealmRole(UUID keycloakUserId, String roleName);

    void removeRealmRole(UUID keycloakUserId, String roleName);

    List<String> listUserRealmRoles(UUID keycloakUserId);

    void resetPassword(UUID keycloakUserId, String password, boolean temporary);

    void requirePasswordChange(UUID keycloakUserId, boolean required);

    Optional<UUID> findUserIdByEmail(String email);

    List<String> searchUserIds(String query);

    List<RealmRole> listRealmRoles();

    Optional<RealmRole> findRealmRole(String name);

    void createRealmRole(RealmRole role);

    void deleteRealmRole(String name);

    Instant lastLogin(UUID keycloakUserId);

    record CreateUser(
            String username,
            String email,
            String firstName,
            String lastName,
            boolean enabled,
            String password,
            boolean temporaryPassword,
            Map<String, String> attributes
    ) {}

    record UpdateUser(
            String email,
            String firstName,
            String lastName,
            Map<String, String> attributes
    ) {}

    record RealmRole(String name, String description, Map<String, String> attributes) {}
}
