package com.nexusgesti.auth.domain.model;

import java.util.UUID;

public record RegisteredUser(
        UUID userId,
        String email,
        String status,
        boolean verificationSent
) {}
