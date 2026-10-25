package com.nexusgesti.boulangerie.production.adapter.in.web;

import com.nexusgesti.boulangerie.config.SecurityConfig;
import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LignePlan;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.port.in.ProductionUseCase;
import com.nexusgesti.common.config.KeycloakJwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductionController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class ProductionControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ProductionUseCase productionUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/plans"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creationPlanSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/plans")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lecturePlansAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(productionUseCase.listPlans()).thenReturn(List.of());
        mvc.perform(get("/api/v1/plans")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void creationPlanAvecRoleADMINEstAcceptee() throws Exception {
        when(productionUseCase.creerPlan(any())).thenReturn(plan());
        mvc.perform(post("/api/v1/plans")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-10-18\",\"atelierCode\":\"ATELIER-1\",\"lignes\":[{\"recetteCode\":\"REC-BAGUETTE\",\"produitCode\":\"BAGUETTE\",\"quantitePrevue\":100}]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void lectureFourneesAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(productionUseCase.listFournees()).thenReturn(List.of());
        mvc.perform(get("/api/v1/fournees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void creationFourneeAvecRoleADMINEstAcceptee() throws Exception {
        when(productionUseCase.creerFournee(any())).thenReturn(fournee());
        mvc.perform(post("/api/v1/fournees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recetteCode\":\"REC-BAGUETTE\",\"produitCode\":\"BAGUETTE\",\"quantitePrevue\":100}"))
                .andExpect(status().isCreated());
    }

    @Test
    void transitionFourneeSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/fournees/" + UUID.randomUUID() + "/lancer")
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    private PlanProduction plan() {
        PlanProduction plan = new PlanProduction();
        plan.setIdPlan(UUID.randomUUID());
        plan.setDate(LocalDate.now());
        plan.setAtelierCode("ATELIER-1");
        LignePlan ligne = new LignePlan();
        ligne.setRecetteCode("REC-BAGUETTE");
        ligne.setProduitCode("BAGUETTE");
        ligne.setQuantitePrevue(new BigDecimal("100"));
        plan.ajouterLigne(ligne);
        return plan;
    }

    private Fournee fournee() {
        Fournee fournee = new Fournee();
        fournee.setIdFournee(UUID.randomUUID());
        fournee.setNumero("FOU-00001");
        fournee.setRecetteCode("REC-BAGUETTE");
        fournee.setProduitCode("BAGUETTE");
        fournee.setQuantitePrevue(new BigDecimal("100"));
        return fournee;
    }
}
