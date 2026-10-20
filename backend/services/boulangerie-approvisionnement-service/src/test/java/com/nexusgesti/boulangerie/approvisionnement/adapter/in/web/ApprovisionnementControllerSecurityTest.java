package com.nexusgesti.boulangerie.approvisionnement.adapter.in.web;

import com.nexusgesti.boulangerie.approvisionnement.config.SecurityConfig;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.in.ApprovisionnementUseCase;
import com.nexusgesti.common.config.KeycloakJwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApprovisionnementController.class)
@Import({SecurityConfig.class, KeycloakJwtConfig.class})
class ApprovisionnementControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ApprovisionnementUseCase approvisionnementUseCase;

    @Test
    void lectureSansTokenEstRefusee401() throws Exception {
        mvc.perform(get("/api/v1/commandes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creationCommandeSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/commandes")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void lectureCommandesAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(approvisionnementUseCase.listCommandes()).thenReturn(List.of());
        mvc.perform(get("/api/v1/commandes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void creationCommandeAvecRoleADMINEstAcceptee() throws Exception {
        when(approvisionnementUseCase.creerCommande(any())).thenReturn(commande());
        mvc.perform(post("/api/v1/commandes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fournisseurTiersCode\":\"FRN-1\",\"date\":\"2026-10-20\","
                                + "\"lignes\":[{\"articleCode\":\"FARINE\",\"designation\":\"Farine\","
                                + "\"quantiteCommandee\":10,\"prixUnitaire\":2.5}]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void creationFournisseurAvecRoleADMINEstAcceptee() throws Exception {
        when(approvisionnementUseCase.creerFournisseur(any())).thenReturn(fournisseur());
        mvc.perform(post("/api/v1/fournisseurs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"FRN-1\",\"raisonSociale\":\"Moulin\",\"delaiLivraisonJours\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void listeFournisseursAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(approvisionnementUseCase.listFournisseurs()).thenReturn(List.of());
        mvc.perform(get("/api/v1/fournisseurs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void transitionCommandeSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/commandes/" + UUID.randomUUID() + "/valider")
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void creationReceptionAvecRoleADMINEstAcceptee() throws Exception {
        when(approvisionnementUseCase.creerReception(any())).thenReturn(reception());
        mvc.perform(post("/api/v1/receptions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commandeId\":\"" + UUID.randomUUID() + "\",\"date\":\"2026-10-21\","
                                + "\"lignes\":[{\"articleCode\":\"FARINE\",\"quantiteRecue\":10,\"prixAchat\":2.5}]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void refusReceptionSansRoleEstRefusee403() throws Exception {
        mvc.perform(post("/api/v1/receptions/" + UUID.randomUUID() + "/refuser")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motif\":\"DLC\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listeReceptionsAvecRoleCUSTOMEREstAutorisee() throws Exception {
        when(approvisionnementUseCase.listReceptions()).thenReturn(List.of());
        mvc.perform(get("/api/v1/receptions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isOk());
    }

    @Test
    void calculBesoinsAvecRoleCUSTOMEREstAutorise() throws Exception {
        when(approvisionnementUseCase.calculerBesoins(any())).thenReturn(List.of());
        mvc.perform(post("/api/v1/besoins/calcule")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"articleCode\":\"FARINE\",\"quantiteDisponible\":0,\"seuil\":10}]"))
                .andExpect(status().isOk());
    }

    private CommandeAchat commande() {
        CommandeAchat commande = new CommandeAchat();
        commande.setIdCommande(UUID.randomUUID());
        commande.setNumero("CMD-00001");
        commande.setFournisseurTiersCode("FRN-1");
        commande.setDate(LocalDate.now());
        return commande;
    }

    private FournisseurBoulangerie fournisseur() {
        FournisseurBoulangerie fournisseur = new FournisseurBoulangerie();
        fournisseur.setIdFournisseur(UUID.randomUUID());
        fournisseur.setCode("FRN-1");
        fournisseur.setRaisonSociale("Moulin");
        return fournisseur;
    }

    private Reception reception() {
        Reception reception = new Reception();
        reception.setIdReception(UUID.randomUUID());
        reception.setNumero("REC-00001");
        reception.setDate(LocalDate.now());
        return reception;
    }
}
