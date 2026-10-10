package com.nexusgesti.assessment.adapter.in.web;

import com.nexusgesti.assessment.config.SecurityConfig;
import com.nexusgesti.assessment.domain.model.Evaluation;
import com.nexusgesti.assessment.domain.port.in.AssessmentUseCase;
import com.nexusgesti.common.config.KeycloakJwtConfig;
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

@WebMvcTest(AssessmentController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class AssessmentControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AssessmentUseCase assessmentUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/evaluations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/evaluations")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(assessmentUseCase.listEvaluations(any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/evaluations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(assessmentUseCase.creerEvaluation(any())).thenReturn(evaluation());
        mvc.perform(post("/api/v1/evaluations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libelle\":\"Devoir 1\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void moyenneLisibleParCUSTOMER() throws Exception {
        when(assessmentUseCase.moyenneEvaluation(any())).thenReturn(new com.nexusgesti.assessment.domain.model.ResultatMoyenne());
        mvc.perform(get("/api/v1/moyennes/evaluation/" + UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private Evaluation evaluation() {
        Evaluation e = new Evaluation();
        e.setIdEvaluation(UUID.randomUUID());
        return e;
    }
}
