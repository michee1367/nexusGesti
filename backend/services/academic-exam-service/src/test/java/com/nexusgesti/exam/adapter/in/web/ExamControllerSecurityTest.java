package com.nexusgesti.exam.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.exam.config.SecurityConfig;
import com.nexusgesti.exam.domain.model.SessionExamen;
import com.nexusgesti.exam.domain.port.in.ExamUseCase;
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

@WebMvcTest(ExamController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class ExamControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ExamUseCase examUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/sessions")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(examUseCase.listSessions(any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/sessions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(examUseCase.creerSession(any())).thenReturn(session());
        mvc.perform(post("/api/v1/sessions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libelle\":\"Session 1\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void deliberationsLisiblesParCUSTOMER() throws Exception {
        when(examUseCase.listDeliberations(any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/deliberations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private SessionExamen session() {
        SessionExamen session = new SessionExamen();
        session.setIdSession(UUID.randomUUID());
        return session;
    }
}
