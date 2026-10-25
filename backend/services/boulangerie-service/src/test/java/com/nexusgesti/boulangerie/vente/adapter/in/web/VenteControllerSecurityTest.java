package com.nexusgesti.boulangerie.vente.adapter.in.web;

import com.nexusgesti.boulangerie.config.SecurityConfig;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;
import com.nexusgesti.boulangerie.vente.domain.port.in.VenteUseCase;
import com.nexusgesti.common.config.KeycloakJwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VenteController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class VenteControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VenteUseCase venteUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/caisses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(venteUseCase.listSessions()).thenReturn(List.of());
        mvc.perform(get("/api/v1/caisses")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ventesLisiblesParCUSTOMER() throws Exception {
        when(venteUseCase.listVentes(null)).thenReturn(List.of());
        mvc.perform(get("/api/v1/ventes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void commandesClientsLisiblesParCUSTOMER() throws Exception {
        when(venteUseCase.listCommandes()).thenReturn(List.of());
        mvc.perform(get("/api/v1/commandes-clients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }
}
