package com.nexusgesti.attendance.adapter.in.web;

import com.nexusgesti.pedagogie.config.SecurityConfig;
import com.nexusgesti.attendance.domain.model.Seance;
import com.nexusgesti.attendance.domain.port.in.AttendanceUseCase;
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

@WebMvcTest(AttendanceController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class AttendanceControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AttendanceUseCase attendanceUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/seances"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/seances")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(attendanceUseCase.listSeances(any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/seances")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(attendanceUseCase.creerSeance(any())).thenReturn(seance());
        mvc.perform(post("/api/v1/seances")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"classeId\":\"11111111-2222-3333-4444-555555555570\",\"date\":\"2026-10-10\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void justificatifsReservesAuRole() throws Exception {
        when(attendanceUseCase.listJustificatifs()).thenReturn(List.of());
        mvc.perform(get("/api/v1/absences/justificatifs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private Seance seance() {
        Seance s = new Seance();
        s.setIdSeance(UUID.randomUUID());
        s.setClasseId(UUID.randomUUID());
        return s;
    }
}
