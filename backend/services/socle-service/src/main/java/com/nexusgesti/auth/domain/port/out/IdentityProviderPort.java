package com.nexusgesti.auth.domain.port.out;

import com.nexusgesti.auth.domain.model.AuthTokens;
import com.nexusgesti.auth.domain.port.in.AuthUseCase;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IdentityProviderPort {

    UUID registerUser(AuthUseCase.RegisterCommand command);

    AuthTokens authenticate(String email, String password);

    AuthTokens refreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    void setUserAttribute(UUID userId, String attribute, String value);

    void assignRealmRole(UUID userId, String roleName);

    UUID findUserIdByEmail(String email);

    Optional<UUID> findTenantIdByEmail(String email);
}
