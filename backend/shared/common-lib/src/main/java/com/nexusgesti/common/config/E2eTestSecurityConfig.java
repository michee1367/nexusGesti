package com.nexusgesti.common.config;

import com.nexusgesti.common.security.E2eTestAuthFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "nexusgesti.security.e2e-test-mode", havingValue = "true")
@EnableMethodSecurity
public class E2eTestSecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain e2eSecurityFilterChain(HttpSecurity http, E2eTestAuthFilter e2eTestAuthFilter)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(e2eTestAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    E2eTestAuthFilter e2eTestAuthFilter() {
        return new E2eTestAuthFilter();
    }
}
