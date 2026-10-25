package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.common.domain.EtablissementId;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.socle.config.SecurityConfig;
import com.nexusgesti.tenant.domain.model.Etablissement;
import com.nexusgesti.tenant.domain.model.ProfilEtablissement;
import com.nexusgesti.tenant.domain.model.StatutEtablissement;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class TenantControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EtablissementUseCase etablissementUseCase;

    private final UUID tenantId = UUID.randomUUID();

    @Test
    void postSansTokenEstRefuse401() throws Exception {
        mvc.perform(post("/api/v1/tenants")
                        .header("X-Tenant-Id", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postSansRoleEstRefuse403() throws Exception {
        mvc.perform(post("/api/v1/tenants")
                        .with(jwt())
                        .header("X-Tenant-Id", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAdminAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(etablissementUseCase.get(any())).thenReturn(etablissement());
        mvc.perform(get("/api/v1/tenants/" + tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .header("X-Tenant-Id", tenantId))
                .andExpect(status().isOk());
    }

    @Test
    void creationAdminAvecRoleADMINEstAcceptee() throws Exception {
        when(etablissementUseCase.creer(any(), any())).thenReturn(etablissement());
        try {
            TenantContext.set(EtablissementId.parse(tenantId.toString()));
            mvc.perform(post("/api/v1/tenants")
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .header("X-Tenant-Id", tenantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"code\":\"NEX\",\"denomination\":\"NexusGesti\"}"))
                    .andExpect(status().isCreated());
        } finally {
            TenantContext.clear();
        }
    }

    @Test
    void identiteEstPubliqueSansToken() throws Exception {
        when(etablissementUseCase.get(any())).thenReturn(etablissement());
        mvc.perform(get("/api/v1/tenants/" + tenantId + "/identite")
                        .header("X-Tenant-Id", tenantId))
                .andExpect(status().isOk());
    }

    @Test
    void changementStatutRefuseAuRoleCUSTOMER() throws Exception {
        mvc.perform(patch("/api/v1/tenants/" + tenantId + "/statut")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .header("X-Tenant-Id", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"ACTIF\"}"))
                .andExpect(status().isForbidden());
    }

    private Etablissement etablissement() {
        Etablissement e = new Etablissement();
        e.setId(tenantId);
        e.setCode("NEX");
        e.setDenomination("NexusGesti");
        e.setProfil(ProfilEtablissement.SECONDAIRE);
        e.setStatut(StatutEtablissement.PREPARATION);
        return e;
    }
}