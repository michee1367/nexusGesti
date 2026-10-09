package com.nexusgesti.identity.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.identity.config.SecurityConfig;
import com.nexusgesti.identity.domain.model.StatutUtilisateur;
import com.nexusgesti.identity.domain.model.Utilisateur;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilisateurController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class UtilisateurControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UtilisateurUseCase utilisateurUseCase;

    private final UUID id = UUID.randomUUID();

    @Test
    void postSansTokenEstRefuse401() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postSansRoleEstRefuse403() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(utilisateurUseCase.get(any())).thenReturn(utilisateur());
        mvc.perform(get("/api/v1/utilisateurs/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void creationAvecRoleADMINEstAcceptee() throws Exception {
        when(utilisateurUseCase.creer(any())).thenReturn(utilisateur());
        when(utilisateurUseCase.get(any())).thenReturn(utilisateur());
        mvc.perform(post("/api/v1/utilisateurs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.c\",\"nom\":\"N\",\"prenom\":\"P\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void rechercheAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(utilisateurUseCase.rechercher(any())).thenReturn(List.of(utilisateur()));
        mvc.perform(get("/api/v1/utilisateurs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private Utilisateur utilisateur() {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(id);
        u.setEmail("a@b.c");
        u.setStatut(StatutUtilisateur.ACTIF);
        return u;
    }
}
