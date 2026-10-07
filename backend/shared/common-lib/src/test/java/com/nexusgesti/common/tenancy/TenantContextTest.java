package com.nexusgesti.common.tenancy;

import com.nexusgesti.common.domain.EtablissementId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantContextTest {

    private static final EtablissementId TENANT_A = EtablissementId.of(UUID.randomUUID());

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void contexteVideParDefault() {
        assertThat(TenantContext.get()).isEmpty();
    }

    @Test
    void requireEchoueSansTenant() {
        assertThatThrownBy(TenantContext::require)
                .isInstanceOf(TenantNotResolvedException.class)
                .hasMessageContaining(TenantContext.HEADER);
    }

    @Test
    void requireRetourneLeTenantRenseigne() {
        TenantContext.set(TENANT_A);
        assertThat(TenantContext.require()).isEqualTo(TENANT_A);
    }

    @Test
    void clearRetireLeThreadLocal() {
        TenantContext.set(TENANT_A);
        TenantContext.clear();
        assertThat(TenantContext.get()).isEmpty();
    }

    @Test
    void refuseTenantNul() {
        assertThatThrownBy(() -> TenantContext.set(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parseRefuseUuidInvalide() {
        assertThatThrownBy(() -> EtablissementId.parse("pas-un-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}