package com.nexusgesti.structure.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.pedagogie.config.SecurityConfig;
import com.nexusgesti.structure.domain.model.Niveau;
import com.nexusgesti.structure.domain.port.in.StructureUseCase;
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

@WebMvcTest(StructureController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class StructureControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StructureUseCase structureUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/niveaux"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/niveaux")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(structureUseCase.listNiveaux()).thenReturn(List.of());
        mvc.perform(get("/api/v1/niveaux")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(structureUseCase.creerNiveau(any())).thenReturn(niveau());
        mvc.perform(post("/api/v1/niveaux")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"6EME\",\"libelle\":\"Sixieme\"}"))
                .andExpect(status().isCreated());
    }

    private Niveau niveau() {
        Niveau n = new Niveau();
        n.setIdNiveau(UUID.randomUUID());
        n.setCode("6EME");
        n.setLibelle("Sixieme");
        return n;
    }
}
