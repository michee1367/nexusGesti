package com.nexusgesti.enrollment.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.pedagogie.config.SecurityConfig;
import com.nexusgesti.enrollment.domain.model.InscriptionUE;
import com.nexusgesti.enrollment.domain.port.in.EnrollmentUseCase;
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

@WebMvcTest(EnrollmentController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class EnrollmentControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EnrollmentUseCase enrollmentUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/inscriptions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/inscriptions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(enrollmentUseCase.listInscriptions()).thenReturn(List.of());
        mvc.perform(get("/api/v1/inscriptions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(enrollmentUseCase.creerInscription(any())).thenReturn(inscription());
        mvc.perform(post("/api/v1/inscriptions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eleveId\":\"11111111-2222-3333-4444-555555555570\",\"ueId\":\"11111111-2222-3333-4444-555555555571\"}"))
                .andExpect(status().isCreated());
    }

    private InscriptionUE inscription() {
        InscriptionUE i = new InscriptionUE();
        i.setIdInscription(UUID.randomUUID());
        i.setEleveId(UUID.randomUUID());
        i.setUeId(UUID.randomUUID());
        return i;
    }
}
