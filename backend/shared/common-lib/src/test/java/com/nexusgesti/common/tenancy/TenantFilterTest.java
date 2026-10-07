package com.nexusgesti.common.tenancy;

import com.nexusgesti.common.domain.EtablissementId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TenantFilterTest {

    private final TenantFilter filter = new TenantFilter();

    private static final EtablissementId TENANT = EtablissementId.of(UUID.randomUUID());

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void refuseRequeteSansHeaderTenant() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/hop/patients");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new jakarta.servlet.FilterChain() {
            public void doFilter(jakarta.servlet.ServletRequest r, jakarta.servlet.ServletResponse s) {
                throw new AssertionError("la chaine ne doit pas etre atteinte");
            }
        });

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentAsString()).contains("TENANT_REQUIRED");
    }

    @Test
    void refuseHeaderTenantInvalide() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/hop/patients");
        request.addHeader(TenantContext.HEADER, "pas-un-uuid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new jakarta.servlet.FilterChain() {
            public void doFilter(jakarta.servlet.ServletRequest r, jakarta.servlet.ServletResponse s) {
                throw new AssertionError("la chaine ne doit pas etre atteinte");
            }
        });

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void renseigneLeContextePendantLEchange() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/hop/patients");
        request.addHeader(TenantContext.HEADER, TENANT.toString());
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (r, s) ->
                assertThat(TenantContext.require()).isEqualTo(TENANT));

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void nettoieLeContexteApresLEchange() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/hop/patients");
        request.addHeader(TenantContext.HEADER, TENANT.toString());

        filter.doFilter(request, new MockHttpServletResponse(), (r, s) -> {
        });

        assertThat(TenantContext.get()).isEmpty();
    }

    @Test
    void nettoieLeContexteApresException() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/hop/patients");
        request.addHeader(TenantContext.HEADER, TENANT.toString());

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                filter.doFilter(request, new MockHttpServletResponse(), (r, s) -> {
                    throw new IllegalStateException("boom");
                })).isInstanceOf(IllegalStateException.class);

        assertThat(TenantContext.get()).isEmpty();
    }

    @Test
    void ignoreActuatorEtDocumentation() throws Exception {
        assertThat(filter.shouldNotFilter(
                new MockHttpServletRequest("GET", "/actuator/health"))).isTrue();
        assertThat(filter.shouldNotFilter(
                new MockHttpServletRequest("GET", "/v3/api-docs"))).isTrue();
        assertThat(filter.shouldNotFilter(
                new MockHttpServletRequest("GET", "/api/v1/hop/patients"))).isFalse();
    }
}