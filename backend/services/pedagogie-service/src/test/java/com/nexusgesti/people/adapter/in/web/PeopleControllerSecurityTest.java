package com.nexusgesti.people.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.pedagogie.config.SecurityConfig;
import com.nexusgesti.people.domain.model.Eleve;
import com.nexusgesti.people.domain.model.StatutEleve;
import com.nexusgesti.people.domain.port.in.PeopleUseCase;
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

@WebMvcTest(PeopleController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class PeopleControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PeopleUseCase peopleUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/eleves"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/eleves")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(peopleUseCase.listEleves()).thenReturn(List.of());
        mvc.perform(get("/api/v1/eleves")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(peopleUseCase.creerEleve(any())).thenReturn(eleve());
        mvc.perform(post("/api/v1/eleves")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Lukusa\",\"prenom\":\"Marie\",\"sexe\":\"F\"}"))
                .andExpect(status().isCreated());
    }

    private Eleve eleve() {
        Eleve e = new Eleve();
        e.setIdEleve(UUID.randomUUID());
        e.setMatricule("ELV-001");
        e.setNom("Lukusa");
        e.setPrenom("Marie");
        e.setStatut(StatutEleve.PREINSCRIT);
        return e;
    }
}
