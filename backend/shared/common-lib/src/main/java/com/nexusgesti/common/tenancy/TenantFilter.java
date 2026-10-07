package com.nexusgesti.common.tenancy;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(TenantContext.HEADER);
        if (header == null || header.isBlank()) {
            reject(response, "Missing " + TenantContext.HEADER + " header");
            return;
        }

        try {
            TenantContext.set(com.nexusgesti.common.domain.EtablissementId.parse(header.trim()));
        } catch (IllegalArgumentException e) {
            reject(response, "Invalid " + TenantContext.HEADER + " header: expected a UUID");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\"TENANT_REQUIRED\",\"message\":\""
                + message + "\",\"requestId\":\"" + UUID.randomUUID() + "\"}");
    }
}