package com.nexusgesti.fees.application.service;

import com.nexusgesti.common.idempotency.IdempotencyService;
import com.nexusgesti.fees.domain.FeesException;
import com.nexusgesti.fees.domain.model.*;
import com.nexusgesti.fees.domain.port.out.FeesRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeesServiceTest {

    private final FeesRepository repository = mock(FeesRepository.class);
    private final IdempotencyService idempotency = mock(IdempotencyService.class);
    private final FeesService service = new FeesService(repository, idempotency);

    @Test
    void creerGrillePositionneStatutEtVersion() {
        when(repository.saveGrille(any())).thenAnswer(inv -> {
            GrilleTarifaire g = inv.getArgument(0);
            g.setId(UUID.randomUUID());
            return g;
        });
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setCode("GRILLE-2026");
        GrilleTarifaire resultat = service.creerGrille(grille);
        assertEquals(Integer.valueOf(1), resultat.getVersion());
        assertEquals(StatutGrille.BROUILLON, resultat.getStatut());
        assertTrue(resultat.isModifiable());
    }

    @Test
    void creerGrilleSansCodeEstRefuse() {
        assertThrows(FeesException.class, () -> service.creerGrille(new GrilleTarifaire()));
    }

    @Test
    void grilleFigeeRefuseNouvelleLigne() {
        UUID id = UUID.randomUUID();
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setId(id);
        grille.figer();
        when(repository.findGrille(id)).thenReturn(Optional.of(grille));
        assertThrows(FeesException.class, () -> service.ajouterLigne(id, new LigneGrille()));
    }

    @Test
    void getGrilleApplicableSansActiveLeve404() {
        UUID anneeId = UUID.randomUUID();
        GrilleTarifaire brouillon = new GrilleTarifaire();
        brouillon.setStatut(StatutGrille.BROUILLON);
        when(repository.listGrilles(anneeId, "PRIMAIRE")).thenReturn(List.of(brouillon));
        FeesException ex = assertThrows(FeesException.class,
                () -> service.getGrilleApplicable(anneeId, "PRIMAIRE"));
        assertEquals("NOT_FOUND", ex.getCode());
    }

    @Test
    void creerAvisGenereEcheancesEtTotaux() {
        when(repository.saveAvis(any())).thenAnswer(inv -> inv.getArgument(0));
        AvisPaiement avis = new AvisPaiement();
        avis.setEleve(UUID.randomUUID());
        LigneFrais ligne = new LigneFrais();
        ligne.setTypeFrais(TypeFrais.SCOLARITE);
        ligne.setMontantBrut(new BigDecimal("100.00"));
        ligne.setNbEcheances(2);
        avis.setLignes(List.of(ligne));

        AvisPaiement resultat = service.creerAvis(avis);

        assertEquals(new BigDecimal("100.00"), resultat.getMontantNet());
        assertEquals(StatutAvis.BROUILLON, resultat.getStatut());
        assertEquals(2, resultat.getLignes().get(0).getEcheances().size());
        assertEquals(new BigDecimal("50.00"), resultat.getLignes().get(0).getEcheances().get(0).getMontant());
    }

    @Test
    void appliquerRemiseRecalculeLeNet() {
        UUID avisId = UUID.randomUUID();
        UUID reductionId = UUID.randomUUID();
        UUID grilleId = UUID.randomUUID();
        AvisPaiement avis = new AvisPaiement();
        avis.setIdAvis(avisId);
        avis.setGrilleId(grilleId);
        LigneFrais ligne = new LigneFrais();
        ligne.setTypeFrais(TypeFrais.SCOLARITE);
        ligne.setMontantBrut(new BigDecimal("200.00"));
        ligne.calculerNet();
        avis.ajouterLigne(ligne);

        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setId(grilleId);
        Reduction reduction = new Reduction();
        reduction.setId(reductionId);
        reduction.setType(TypeReduction.POURCENTAGE);
        reduction.setValeur(new BigDecimal("10"));
        grille.ajouterReduction(reduction);

        when(repository.findAvis(avisId)).thenReturn(Optional.of(avis));
        when(repository.findGrille(grilleId)).thenReturn(Optional.of(grille));
        when(repository.saveAvis(any())).thenAnswer(inv -> inv.getArgument(0));

        AvisPaiement resultat = service.appliquerRemise(avisId, reductionId, new ContexteReduction());

        assertEquals(new BigDecimal("20.00"), resultat.getMontantReduction());
        assertEquals(new BigDecimal("180.00"), resultat.getMontantNet());
    }

    @Test
    void creerPaiementExigeCleIdempotence() {
        assertThrows(FeesException.class, () -> service.creerPaiement(new Paiement(), null));
    }

    @Test
    void creerPaiementEnregistreLaReponse() {
        Paiement paiement = paiement();
        UUID id = UUID.randomUUID();
        when(idempotency.getCachedResponse(any(), any())).thenReturn(Optional.empty());
        when(idempotency.tryAcquireLock("cle")).thenReturn(true);
        when(repository.savePaiement(any())).thenAnswer(inv -> {
            Paiement p = inv.getArgument(0);
            p.setIdPaiement(id);
            return p;
        });
        service.creerPaiement(paiement, "cle");
        verify(idempotency).storeResponse("cle", id.toString());
        verify(idempotency).releaseLock("cle");
    }

    @Test
    void creerPaiementRelitLaReponseCached() {
        UUID id = UUID.randomUUID();
        Paiement existant = paiement();
        existant.setIdPaiement(id);
        when(idempotency.getCachedResponse(any(), any())).thenReturn(Optional.of(id.toString()));
        when(repository.findPaiement(id)).thenReturn(Optional.of(existant));

        Paiement resultat = service.creerPaiement(paiement(), "cle");

        assertSame(existant, resultat);
        verify(repository, never()).savePaiement(any());
    }

    @Test
    void validerPaiementImputeLesEcheancesEtGenereLeCoupon() {
        UUID avisId = UUID.randomUUID();
        UUID paiementId = UUID.randomUUID();
        UUID anneeId = UUID.randomUUID();

        AvisPaiement avis = new AvisPaiement();
        avis.setIdAvis(avisId);
        avis.setAnnee(anneeId);
        avis.setEleve(UUID.randomUUID());
        avis.setDevise("USD");
        avis.setStatut(StatutAvis.EMIS);
        LigneFrais ligne = new LigneFrais();
        ligne.setId(UUID.randomUUID());
        ligne.setTypeFrais(TypeFrais.SCOLARITE);
        ligne.setMontantBrut(new BigDecimal("100.00"));
        ligne.setNbEcheances(1);
        ligne.calculerNet();
        ligne.genererEcheances();
        avis.ajouterLigne(ligne);

        Paiement paiement = paiement();
        paiement.setIdPaiement(paiementId);
        paiement.setAvis(avisId);
        paiement.setEleve(avis.getEleve());
        paiement.setMontant(new BigDecimal("100.00"));

        when(repository.findPaiement(paiementId)).thenReturn(Optional.of(paiement));
        when(repository.findAvis(avisId)).thenReturn(Optional.of(avis));
        when(repository.countCouponsAnnee(anneeId)).thenReturn(0L);
        when(repository.saveAvis(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.saveCoupon(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.savePaiement(any())).thenAnswer(inv -> inv.getArgument(0));

        Paiement resultat = service.validerPaiement(paiementId);

        assertEquals(StatutPaiement.VALIDE, resultat.getStatut());
        assertNotNull(resultat.getCouponGenere());
        assertEquals(StatutAvis.REGLE, avis.getStatut());
        assertEquals(new BigDecimal("100.00"), avis.getMontantRegle());
        assertEquals(StatutEcheance.REGLEE, ligne.getEcheances().get(0).getStatut());
        assertEquals(1, resultat.getImputations().size());
        verify(repository).saveCoupon(any());
    }

    private Paiement paiement() {
        Paiement paiement = new Paiement();
        paiement.setEleve(UUID.randomUUID());
        paiement.setMontant(new BigDecimal("100.00"));
        paiement.setMoyen(MoyenPaiement.ESPECES);
        return paiement;
    }
}