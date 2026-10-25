package com.nexusgesti.boulangerie.approvisionnement.application.service;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Rapprochement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.in.ApprovisionnementUseCase;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementEventPublisher;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApprovisionnementServiceTest {

    private ApprovisionnementRepository repository;
    private ApprovisionnementEventPublisher eventPublisher;
    private ApprovisionnementService service;

    @BeforeEach
    void setUp() {
        repository = mock(ApprovisionnementRepository.class);
        eventPublisher = mock(ApprovisionnementEventPublisher.class);
        service = new ApprovisionnementService(repository, eventPublisher);
    }

    private CommandeAchat commandeEnvoyee() {
        CommandeAchat commande = new CommandeAchat();
        commande.setIdCommande(UUID.randomUUID());
        commande.setNumero("CMD-00001");
        commande.setFournisseurTiersCode("FRN-1");
        commande.setDate(LocalDate.of(2026, 10, 20));
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setArticleCode("FARINE");
        ligne.setDesignation("Farine de ble");
        ligne.setQuantiteCommandee(BigDecimal.valueOf(10));
        ligne.setPrixUnitaire(new BigDecimal("2.50"));
        ligne.setTauxTva(BigDecimal.ZERO);
        commande.ajouterLigne(ligne);
        commande.valider();
        commande.envoyer();
        return commande;
    }

    private Reception receptionPour(CommandeAchat commande) {
        Reception reception = new Reception();
        reception.setIdReception(UUID.randomUUID());
        reception.setNumero("REC-00001");
        reception.setCommandeId(commande.getIdCommande());
        reception.setDate(LocalDate.now());
        LigneReception ligne = new LigneReception();
        ligne.setArticleCode("FARINE");
        ligne.setQuantiteRecue(BigDecimal.valueOf(10));
        ligne.setPrixAchat(new BigDecimal("2.50"));
        reception.ajouterLigne(ligne);
        return reception;
    }

    @Test
    void creerCommandeGenereLeNumeroEtRemetEnBrouillon() {
        when(repository.countCommandes()).thenReturn(0);
        when(repository.saveCommande(any())).thenAnswer(i -> i.getArgument(0));

        CommandeAchat commande = new CommandeAchat();
        commande.setStatut(StatutCommandeAchat.ENVOYEE);
        commande.setFournisseurTiersCode("FRN-1");
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setArticleCode("FARINE");
        ligne.setQuantiteRecue(BigDecimal.valueOf(99));
        commande.getLignes().add(ligne);

        CommandeAchat cree = service.creerCommande(commande);

        assertEquals("CMD-00001", cree.getNumero());
        assertEquals(StatutCommandeAchat.BROUILLON, cree.getStatut());
        assertEquals(0, cree.getLignes().get(0).getQuantiteRecue().compareTo(BigDecimal.ZERO));
    }

    @Test
    void validerCommandePublieLEvenement() {
        CommandeAchat commande = new CommandeAchat();
        commande.setIdCommande(UUID.randomUUID());
        commande.setFournisseurTiersCode("FRN-1");
        commande.setDate(LocalDate.now());
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setArticleCode("FARINE");
        ligne.setDesignation("Farine");
        ligne.setQuantiteCommandee(BigDecimal.TEN);
        ligne.setPrixUnitaire(BigDecimal.ONE);
        commande.ajouterLigne(ligne);
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.saveCommande(any())).thenAnswer(i -> i.getArgument(0));

        service.validerCommande(commande.getIdCommande());

        assertEquals(StatutCommandeAchat.VALIDEE, commande.getStatut());
        verify(eventPublisher).publierCommandeValidee(any(CommandeAchat.class));
    }

    @Test
    void creerReceptionSurCommandeNonEnvoyeeEstRefusee() {
        CommandeAchat commande = commandeEnvoyee();
        commande.setStatut(StatutCommandeAchat.VALIDEE);
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                () -> service.creerReception(receptionPour(commande)));
        assertEquals("ETAT_INVALIDE", ex.getCode());
    }

    @Test
    void creerReceptionGenerNumeroEtControleQuantite() {
        CommandeAchat commande = commandeEnvoyee();
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.countReceptions()).thenReturn(0);
        when(repository.saveReception(any())).thenAnswer(i -> i.getArgument(0));

        Reception reception = service.creerReception(receptionPour(commande));

        assertEquals("REC-00001", reception.getNumero());
        assertEquals(commande.getNumero(), reception.getCommandeNumero());
        assertEquals(StatutReception.ATTENDUE, reception.getStatut());
        assertEquals(0, reception.getLignes().get(0).getQuantiteCommandee().compareTo(BigDecimal.valueOf(10)));
    }

    @Test
    void creerReceptionAuDelaDuResteEstRefusee() {
        CommandeAchat commande = commandeEnvoyee();
        commande.getLignes().get(0).setQuantiteRecue(BigDecimal.valueOf(4));
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));

        Reception reception = receptionPour(commande);
        reception.getLignes().get(0).setQuantiteRecue(BigDecimal.valueOf(7));

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                () -> service.creerReception(reception));
        assertEquals("INVALIDE", ex.getCode());
    }

    @Test
    void accepterReceptionMetAJourLaCommandeEtPublieLesEvenements() {
        CommandeAchat commande = commandeEnvoyee();
        Reception reception = receptionPour(commande);
        reception.controler();
        when(repository.findReception(any())).thenReturn(Optional.of(reception));
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.saveReception(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.saveCommande(any())).thenAnswer(i -> i.getArgument(0));

        Reception acceptee = service.accepterReception(reception.getIdReception());

        assertEquals(StatutReception.ACCEPTEE, acceptee.getStatut());
        assertEquals(0, commande.getLignes().get(0).getQuantiteRecue().compareTo(BigDecimal.valueOf(10)));
        assertEquals(StatutCommandeAchat.RECEPTIONNEE, commande.getStatut());
        verify(eventPublisher).publierReceptionAcceptee(any(Reception.class));
        ArgumentCaptor<MouvementStockIntention> captor = ArgumentCaptor.forClass(MouvementStockIntention.class);
        verify(eventPublisher).publierMouvementStock(captor.capture());
        assertEquals("FARINE", captor.getValue().articleCode());
        assertEquals(0, captor.getValue().quantite().compareTo(BigDecimal.valueOf(10)));
        assertEquals(0, captor.getValue().prixAchat().compareTo(new BigDecimal("2.50")));
    }

    @Test
    void accepterReceptionPartielleMarqueLaCommandePartielle() {
        CommandeAchat commande = commandeEnvoyee();
        Reception reception = receptionPour(commande);
        reception.getLignes().get(0).setQuantiteRecue(BigDecimal.valueOf(4));
        reception.controler();
        when(repository.findReception(any())).thenReturn(Optional.of(reception));
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.saveReception(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.saveCommande(any())).thenAnswer(i -> i.getArgument(0));

        service.accepterReception(reception.getIdReception());

        assertEquals(StatutCommandeAchat.PARTIELLE, commande.getStatut());
        assertTrue(commande.estRecevable());
    }

    @Test
    void refuserReceptionPublieLEvenementSansToucherLaCommande() {
        CommandeAchat commande = commandeEnvoyee();
        Reception reception = receptionPour(commande);
        reception.controler();
        when(repository.findReception(any())).thenReturn(Optional.of(reception));
        when(repository.saveReception(any())).thenAnswer(i -> i.getArgument(0));

        service.refuserReception(reception.getIdReception(), "DLC");

        assertEquals(StatutReception.REFUSEE, reception.getStatut());
        verify(eventPublisher).publierReceptionRefusee(any(Reception.class), eq("DLC"));
        verify(repository, never()).saveCommande(any());
    }

    @Test
    void calculerBesoinsPublieLesArticlesSousSeuil() {
        List<ApprovisionnementUseCase.SaisieBesoin> saisies = List.of(
                new ApprovisionnementUseCase.SaisieBesoin("FARINE", "Farine",
                        BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, 2),
                new ApprovisionnementUseCase.SaisieBesoin("SEL", "Sel",
                        BigDecimal.valueOf(100), BigDecimal.TEN, BigDecimal.ZERO, 2));

        List<BesoinApprovisionnement> besoins = service.calculerBesoins(saisies);

        assertEquals(2, besoins.size());
        verify(eventPublisher).publierBesoinsSousSeuil(argThat(l -> l.size() == 1));
    }

    @Test
    void listerSousSeuilNeGardeQueLesAlertes() {
        List<ApprovisionnementUseCase.SaisieBesoin> saisies = List.of(
                new ApprovisionnementUseCase.SaisieBesoin("FARINE", "Farine",
                        BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, null),
                new ApprovisionnementUseCase.SaisieBesoin("SEL", "Sel",
                        BigDecimal.valueOf(100), BigDecimal.TEN, BigDecimal.ZERO, null));

        List<BesoinApprovisionnement> alertes = service.listerSousSeuil(saisies);

        assertEquals(1, alertes.size());
        assertEquals("FARINE", alertes.get(0).articleCode());
    }

    @Test
    void calculerBesoinsSansSaisieEstRefuse() {
        assertThrows(ApprovisionnementException.class, () -> service.calculerBesoins(List.of()));
    }

    @Test
    void rapprochementSignaleLEcartDePrix() {
        CommandeAchat commande = commandeEnvoyee();
        Reception reception = receptionPour(commande);
        reception.getLignes().get(0).setPrixAchat(new BigDecimal("3.00"));
        reception.controler();
        reception.accepter();
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.findReceptionsByCommande(any())).thenReturn(List.of(reception));

        Rapprochement rapprochement = service.rapprocherCommande(commande.getIdCommande());

        assertFalse(rapprochement.concordant());
        assertEquals(1, rapprochement.ecarts().size());
        assertEquals(0, rapprochement.ecarts().get(0).ecartPrixAchat().compareTo(new BigDecimal("0.50")));
        assertEquals(0, rapprochement.montantReceptionsTTC().compareTo(new BigDecimal("30.00")));
    }

    @Test
    void rapprochementConcordantSansEcart() {
        CommandeAchat commande = commandeEnvoyee();
        Reception reception = receptionPour(commande);
        reception.controler();
        reception.accepter();
        when(repository.findCommande(any())).thenReturn(Optional.of(commande));
        when(repository.findReceptionsByCommande(any())).thenReturn(List.of(reception));

        Rapprochement rapprochement = service.rapprocherCommande(commande.getIdCommande());

        assertTrue(rapprochement.concordant());
        assertTrue(rapprochement.ecarts().isEmpty());
        assertEquals(commande.getStatut().name(), rapprochement.statutCommande());
    }

    @Test
    void evaluerFournisseurCompteCommandesEtReceptions() {
        FournisseurBoulangerie fournisseur = new FournisseurBoulangerie();
        fournisseur.setIdFournisseur(UUID.randomUUID());
        fournisseur.setCode("FRN-1");
        fournisseur.setRaisonSociale("Moulin");
        fournisseur.setDelaiLivraisonJours(3);
        when(repository.findFournisseur(any())).thenReturn(Optional.of(fournisseur));

        CommandeAchat commande = commandeEnvoyee();
        commande.setFournisseurTiersCode("FRN-1");
        Reception acceptee = receptionPour(commande);
        acceptee.controler();
        acceptee.accepter();
        Reception refusee = receptionPour(commande);
        refusee.setNumero("REC-00002");
        refusee.controler();
        refusee.refuser("Abime");
        when(repository.listCommandes()).thenReturn(List.of(commande));
        when(repository.findReceptionsByCommande(any())).thenReturn(List.of(acceptee, refusee));

        ApprovisionnementUseCase.EvaluationFournisseur eval = service.evaluerFournisseur(
                fournisseur.getIdFournisseur());

        assertEquals(1, eval.nbCommandes());
        assertEquals(1, eval.nbReceptionsHonorees());
        assertEquals(1, eval.nbReceptionsRefusees());
        assertTrue(eval.fiable());
    }

    @Test
    void commandeInconnueLeveNotFound() {
        when(repository.findCommande(any())).thenReturn(Optional.empty());

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                () -> service.getCommande(UUID.randomUUID()));
        assertEquals("NOT_FOUND", ex.getCode());
    }
}
