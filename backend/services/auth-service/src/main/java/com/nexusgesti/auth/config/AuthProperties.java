package com.nexusgesti.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexusgesti.auth")
public record AuthProperties(
        String mfaIssuer,
        String encryptionKey,
        int sessionRefreshDays,
        int maxSessionsPerUser,
        int loginMaxAttempts,
        int loginLockMinutes
) {}
