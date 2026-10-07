package com.nexusgesti.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Cle d'idempotence portee par une chaine, et non un UUID : l'evenement
 * hopital.acte.facturable se dedoublonne sur sourceId + codeActe, cle composite
 * qu'un UUID ne peut pas exprimer.
 */
@Component
public class IdempotencyService {

    private static final Duration TTL = Duration.ofHours(24);
    private static final Duration LOCK_TTL = Duration.ofMinutes(5);
    private static final String PREFIX = "idempotency:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public IdempotencyService(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> getCachedResponse(String key, Class<T> responseType) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("idempotency key is required");
        }
        String cached = redis.opsForValue().get(PREFIX + key);
        if (cached == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(cached, responseType));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void storeResponse(String key, Object response) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("idempotency key is required");
        }
        try {
            redis.opsForValue().set(PREFIX + key, objectMapper.writeValueAsString(response), TTL);
        } catch (Exception e) {
            throw new IdempotencyStoreException("Failed to store idempotency response", e);
        }
    }

    public boolean tryAcquireLock(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("idempotency key is required");
        }
        Boolean acquired = redis.opsForValue().setIfAbsent(PREFIX + key + ":lock", "1", LOCK_TTL);
        return Boolean.TRUE.equals(acquired);
    }

    public void releaseLock(String key) {
        redis.delete(PREFIX + key + ":lock");
    }

    public static class IdempotencyStoreException extends RuntimeException {
        public IdempotencyStoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}