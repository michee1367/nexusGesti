package com.nexusgesti.common.config;

import com.nexusgesti.common.security.InternalServiceAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "nexusgesti.security.oauth2.enabled", havingValue = "true", matchIfMissing = true)
public class OAuth2ResourceServerConfig {

    //@Bean
    @Bean(name = "oauth2SecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   InternalServiceAuthFilter internalFilter,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/internal/**").hasRole("SERVICE")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .addFilterBefore(internalFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public InternalServiceAuthFilter internalServiceAuthFilter(
            @Value("${nexusgesti.internal-service-token:dev-internal-token}") String internalToken) {
        return new InternalServiceAuthFilter(internalToken);
    }
}
