package com.nexusgesti.gateway.filter;

import com.nexusgesti.common.domain.TenantHeaders;
import com.nexusgesti.common.domain.EtablissementId;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reseau de confiance multi-tenant : un client qui joint un etablissement
 * inexistant est rejete ici, avant d'atteindre un service. Sans cela, un UUID
 * arbitraire traverse la passerelle et n'echoue qu'en base, ou jamais.
 */
@Component
public class TenantHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isExempt(exchange)) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(TenantHeaders.HEADER);
        if (header == null || header.isBlank()) {
            return reject(exchange, "Missing " + TenantHeaders.HEADER + " header");
        }

        try {
            EtablissementId.parse(header.trim());
        } catch (IllegalArgumentException e) {
            return reject(exchange, "Invalid " + TenantHeaders.HEADER + " header: expected a UUID");
        }

        return chain.filter(exchange);
    }

    private boolean isExempt(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/api-docs")
                || path.equals("/api/v1/socle/auth/resolve-tenant")
                || path.equals("/fallback");
    }

    private Mono<Void> reject(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().writeWith(
                Mono.fromSupplier(() -> exchange.getResponse().bufferFactory()
                        .wrap(message.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TenantHeaderFilter;
    }

    @Override
    public int hashCode() {
        return TenantHeaderFilter.class.hashCode();
    }
}