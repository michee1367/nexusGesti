package com.nexusgesti.fees.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.fees.config.SecurityConfig;
import com.nexusgesti.fees.domain.model.GrilleTarifaire;
import com.nexusgesti.fees.domain.model.Paiement;
import com.nexusgesti.fees.domain.port.in.FeesUseCase;
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

@WebMvcTest(FeesController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class FeesControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FeesUseCase feesUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/frais/grilles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/frais/grilles")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(feesUseCase.listGrilles(any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/frais/grilles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(feesUseCase.creerGrille(any())).thenReturn(grille());
        mvc.perform(post("/api/v1/frais/grilles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"GRILLE-2026\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void paiementExigeHeaderIdempotenceSeulementEnPerMetre() throws Exception {
        when(feesUseCase.creerPaiement(any(), any())).thenReturn(paiement());
        mvc.perform(post("/api/v1/paiements")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .header("Idempotency-Key", "cle-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"montant\":100.00,\"moyen\":\"ESPECES\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void couponsVerifiablesParCUSTOMER() throws Exception {
        when(feesUseCase.verifierCoupon(any())).thenReturn(true);
        mvc.perform(post("/api/v1/coupons/{id}/verifier", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private GrilleTarifaire grille() {
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setId(UUID.randomUUID());
        grille.setCode("GRILLE-2026");
        return grille;
    }

    private Paiement paiement() {
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(UUID.randomUUID());
        paiement.setMoyen(com.nexusgesti.fees.domain.model.MoyenPaiement.ESPECES);
        return paiement;
    }
}