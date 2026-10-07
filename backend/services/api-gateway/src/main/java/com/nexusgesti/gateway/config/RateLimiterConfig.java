package com.nexusgesti.gateway.config;

import com.nexusgesti.common.domain.TenantHeaders;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * Le debit est limite par etablissement, pas par utilisateur : un etablissement
 * mutate depuis plusieurs postes et le plafond doit etre lisible sur la facture.
 * L'utilisateur reste le second niveau, refine si l'etablissement est connu.
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver tenantKeyResolver() {
        return exchange -> {
            String tenant = exchange.getRequest().getHeaders().getFirst(TenantHeaders.HEADER);
            if (tenant != null && !tenant.isBlank()) {
                return Mono.just("tenant:" + tenant.trim());
            }
            return exchange.getPrincipal()
                    .map(Principal::getName)
                    .map(user -> "user:" + user)
                    .defaultIfEmpty("ip:" + remoteAddress(exchange));
        };
    }

    private String remoteAddress(org.springframework.web.server.ServerWebExchange exchange) {
        var address = exchange.getRequest().getRemoteAddress();
        return address != null && address.getAddress() != null
                ? address.getAddress().getHostAddress()
                : "anonymous";
    }
}