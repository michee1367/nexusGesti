package com.nexusgesti.identity.domain.model;

import com.nexusgesti.identity.domain.IdentityException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilisateurTest {

    @Test
    void activerDepuisInvite() {
        Utilisateur u = new Utilisateur();
        u.setStatut(StatutUtilisateur.INVITE);
        u.activer();
        assertThat(u.getStatut()).isEqualTo(StatutUtilisateur.ACTIF);
    }

    @Test
    void activerDepuisSupprimeEstInterdit() {
        Utilisateur u = new Utilisateur();
        u.setStatut(StatutUtilisateur.SUPPRIME);
        assertThatThrownBy(u::activer)
                .isInstanceOf(IdentityException.class)
                .hasFieldOrPropertyWithValue("code", "TRANSITION_INVALIDE");
    }

    @Test
    void desactiverExigeUnMotif() {
        Utilisateur u = new Utilisateur();
        u.setStatut(StatutUtilisateur.ACTIF);
        assertThatThrownBy(() -> u.desactiver(" "))
                .isInstanceOf(IdentityException.class)
                .hasFieldOrPropertyWithValue("code", "MOTIF_REQUIS");
    }

    @Test
    void desactiverAjouteLeMotif() {
        Utilisateur u = new Utilisateur();
        u.setStatut(StatutUtilisateur.ACTIF);
        u.desactiver("Mutation");
        assertThat(u.getStatut()).isEqualTo(StatutUtilisateur.DESACTIF);
        assertThat(u.getMotifsDesactivation()).hasSize(1);
        assertThat(u.getMotifsDesactivation().getFirst().getMotif()).isEqualTo("Mutation");
    }

    @Test
    void suspendreSeulementDepuisActif() {
        Utilisateur u = new Utilisateur();
        u.setStatut(StatutUtilisateur.INVITE);
        assertThatThrownBy(() -> u.suspendre(java.time.LocalDate.now()))
                .isInstanceOf(IdentityException.class);
    }

    @Test
    void attribuerEtRetirerRole() {
        Utilisateur u = new Utilisateur();
        Role role = new Role();
        role.setCode("ADMIN");
        u.attribuerRole(role);
        assertThat(u.aRole("ADMIN")).isTrue();
        assertThat(u.getRolesActifs()).hasSize(1);
        u.retirerRole("ADMIN");
        assertThat(u.aRole("ADMIN")).isFalse();
    }

    @Test
    void attribuerDeuxFoisLeMemeRoleNeDupliquePas() {
        Utilisateur u = new Utilisateur();
        Role role = new Role();
        role.setCode("ADMIN");
        u.attribuerRole(role);
        u.attribuerRole(role);
        assertThat(u.getRolesActifs()).hasSize(1);
    }
}
