package com.nexusgesti.boulangerie.produit.adapter.in.web;

import com.nexusgesti.common.config.KeycloakJwtConfig;
import com.nexusgesti.boulangerie.produit.config.SecurityConfig;
import com.nexusgesti.boulangerie.produit.domain.model.Produit;
import com.nexusgesti.boulangerie.produit.domain.model.UniteVente;
import com.nexusgesti.boulangerie.produit.domain.port.in.ProduitUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProduitController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class ProduitControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ProduitUseCase produitUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/produits"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ecritureSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/produits")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(produitUseCase.listProduits(any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/produits")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void ecritureAvecRoleADMINEstAcceptee() throws Exception {
        when(produitUseCase.creerProduit(any())).thenReturn(produit());
        mvc.perform(post("/api/v1/produits")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"BAGUETTE\",\"designation\":\"Baguette tradition\",\"uniteVente\":\"PIECE\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void gammesLisiblesParCUSTOMER() throws Exception {
        when(produitUseCase.listGammes()).thenReturn(List.of());
        mvc.perform(get("/api/v1/gammes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void recettesLisiblesParCUSTOMER() throws Exception {
        when(produitUseCase.listRecettes(any())).thenReturn(List.of());
        mvc.perform(get("/api/v1/recettes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    private Produit produit() {
        Produit produit = new Produit();
        produit.setIdProduit(UUID.randomUUID());
        produit.setCode("BAGUETTE");
        produit.setUniteVente(UniteVente.PIECE);
        produit.setPrixVenteHT(new BigDecimal("0.80"));
        return produit;
    }
}
