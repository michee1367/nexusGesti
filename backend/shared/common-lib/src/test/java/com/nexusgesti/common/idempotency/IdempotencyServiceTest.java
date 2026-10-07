package com.nexusgesti.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdempotencyServiceTest {

    private static final String CLE_COMPOSITE =
            "hopital-plateau-technique-service:" + UUID.randomUUID() + ":LABO_GLYCEMIE";

    private StringRedisTemplate redis;
    private ValueOperations<String, String> values;
    private IdempotencyService service;

    @BeforeEach
    void setUp() {
        redis = mock(StringRedisTemplate.class);
        values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        service = new IdempotencyService(redis, new ObjectMapper());
    }

    @Test
    void accepteUneCleCompositeSourceIdPlusCodeActe() {
        when(values.get(anyString())).thenReturn("{\"numero\":\"P-1\"}");

        Optional<PaiementReponse> cached =
                service.getCachedResponse(CLE_COMPOSITE, PaiementReponse.class);

        assertThat(cached).isPresent();
        assertThat(cached.get().numero()).isEqualTo("P-1");
    }

    @Test
    void pasDeReponseCachee() {
        when(values.get(anyString())).thenReturn(null);
        assertThat(service.getCachedResponse(CLE_COMPOSITE, PaiementReponse.class)).isEmpty();
    }

    @Test
    void refuseCleVide() {
        assertThatThrownBy(() -> service.getCachedResponse(" ", PaiementReponse.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void stockeReponseSousLaCleFournie() {
        service.storeResponse(CLE_COMPOSITE, new PaiementReponse("P-1"));
        verify(values).set("idempotency:" + CLE_COMPOSITE, "{\"numero\":\"P-1\"}",
                java.time.Duration.ofHours(24));
    }

    @Test
    void verrouLibreResteLibre() {
        when(values.setIfAbsent(anyString(), anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(Boolean.TRUE);
        assertThat(service.tryAcquireLock(CLE_COMPOSITE)).isTrue();
    }

    @Test
    void verrouDejaPrisEstRefuse() {
        when(values.setIfAbsent(anyString(), anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(Boolean.FALSE);
        assertThat(service.tryAcquireLock(CLE_COMPOSITE)).isFalse();
    }

    record PaiementReponse(String numero) {
    }
}