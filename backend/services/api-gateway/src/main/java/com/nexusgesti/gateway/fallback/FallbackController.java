package com.nexusgesti.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // Toutes les methodes : le fallback du CircuitBreaker reutilise la methode
    // de la requete d'origine. Un @GetMapping seul repond 405 sur un POST.
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.PATCH, RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, Object>> fallback(ServerWebExchange exchange) {
        return Mono.just(Map.of(
                "error", "SERVICE_UNAVAILABLE",
                "message", "Le service est temporairement indisponible. Réessayez dans quelques instants.",
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value()
        ));
    }
}
