package com.nexusgesti.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    void correspondAVerbeEtRessource() {
        Permission p = new Permission();
        p.setAction("LIRE");
        p.setRessource("utilisateurs");
        assertThat(p.correspondA("lire", "UTILISATEURS")).isTrue();
        assertThat(p.correspondA("ecrire", "utilisateurs")).isFalse();
    }

    @Test
    void correspondARefuseLesNulls() {
        Permission p = new Permission();
        p.setAction("LIRE");
        p.setRessource("utilisateurs");
        assertThat(p.correspondA(null, "utilisateurs")).isFalse();
        assertThat(p.correspondA("lire", null)).isFalse();
    }
}
