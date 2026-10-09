package com.nexusgesti.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RolePermissionTest {

    @Test
    void sansEcheanceNestJamaisExpiree() {
        RolePermission rp = new RolePermission();
        assertThat(rp.estExpiree(LocalDate.now())).isFalse();
    }

    @Test
    void expireeApresLEcheance() {
        RolePermission rp = new RolePermission();
        rp.setExpireLe(LocalDate.of(2026, 1, 1));
        assertThat(rp.estExpiree(LocalDate.of(2026, 6, 1))).isTrue();
        assertThat(rp.estExpiree(LocalDate.of(2025, 12, 31))).isFalse();
    }
}
