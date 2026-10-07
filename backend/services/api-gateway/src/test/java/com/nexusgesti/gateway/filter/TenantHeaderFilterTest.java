package com.nexusgesti.gateway.filter;

import com.nexusgesti.common.domain.TenantHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class TenantHeaderFilterTest {

    private final TenantHeaderFilter filter = new TenantHeaderFilter();

    @Test
    void laissePasserUnEtablissementValide() {
        MockServerWebExchange exchange = exchangeWithTenant(UUID.randomUUID().toString());
        AtomicBoolean reached = new AtomicBoolean();

        filter.filter(exchange, e -> {
            reached.set(true);
            return Mono.empty();
        }).block();

        assertThat(reached).isTrue();
    }

    @Test
    void rejetteUnHeaderAbsent() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/hop/patients"));

        filter.filter(exchange, e -> Mono.error(new AssertionError("jamais appele"))).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejetteUnUuidInvalide() {
        MockServerWebExchange exchange = exchangeWithTenant("pas-un-uuid");

        filter.filter(exchange, e -> Mono.error(new AssertionError("jamais appele"))).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void exempteActuatorEtDocumentation() {
        for (String path : new String[]{"/actuator/health", "/v3/api-docs", "/swagger-ui/index.html"}) {
            MockServerWebExchange exchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get(path));
            AtomicBoolean reached = new AtomicBoolean();

            filter.filter(exchange, e -> {
                reached.set(true);
                return Mono.empty();
            }).block();

            assertThat(reached).as(path).isTrue();
        }
    }

    @Test
    void exempteLaRouteDeFallback() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/fallback"));
        AtomicBoolean reached = new AtomicBoolean();

        filter.filter(exchange, e -> {
            reached.set(true);
            return Mono.empty();
        }).block();

        assertThat(reached).isTrue();
    }

    @Test
    void executeAvantLesFiltresDeSecurite() {
        assertThat(filter.getOrder()).isLessThanOrEqualTo(-100);
    }

    private MockServerWebExchange exchangeWithTenant(String value) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/hop/patients")
                        .header(TenantHeaders.HEADER, value));
    }
}