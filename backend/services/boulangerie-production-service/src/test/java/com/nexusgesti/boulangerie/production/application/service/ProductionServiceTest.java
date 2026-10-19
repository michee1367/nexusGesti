package com.nexusgesti.boulangerie.production.application.service;

import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LigneConsommation;
import com.nexusgesti.boulangerie.production.domain.model.LignePlan;
import com.nexusgesti.boulangerie.production.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.production.domain.model.PerteProduction;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.model.TypeMouvementStock;
import com.nexusgesti.boulangerie.production.domain.model.TypePerte;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionEventPublisher;
import com.nexusgesti.boulangerie.production.domain.port.out.ProductionRepository;
import com.nexusgesti.boulangerie.production.domain.port.out.RecetteProvider;
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

class ProductionServiceTest {

    private ProductionRepository repository;
    private ProductionEventPublisher eventPublisher;
    private RecetteProvider recetteProvider;
    private ProductionService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductionRepository.class);
        eventPublisher = mock(ProductionEventPublisher.class);
        recetteProvider = mock(RecetteProvider.class);
        service = new ProductionService(repository, eventPublisher, recetteProvider);
    }

    private PlanProduction planAvecLigne() {
        PlanProduction plan = new PlanProduction();
        plan.setDate(LocalDate.of(2026, 10, 17));
        LignePlan ligne = new LignePlan();
        ligne.setRecetteCode("REC-BAG");
        ligne.setQuantitePrevue(BigDecimal.TEN);
        plan.ajouterLigne(ligne);
        return plan;
    }

    @Test
    void creerPlanAgregeLesBesoinsDesRecettes() {
        when(recetteProvider.resoudreBesoins(eq("REC-BAG"), isNull(), eq(BigDecimal.TEN)))
                .thenReturn(List.of(new BesoinMatiere("FARINE", "Farine", BigDecimal.TEN, BigDecimal.valueOf(11), "KG")));
        when(repository.savePlan(any())).thenAnswer(i -> i.getArgument(0));

        PlanProduction cree = service.creerPlan(planAvecLigne());

        assertEquals(1, cree.getBesoinsMatiere().size());
        assertEquals(0, cree.getBesoinsMatiere().get(0).quantiteBrute().compareTo(BigDecimal.valueOf(11)));
        assertEquals("REC-BAG", cree.getLignes().get(0).getRecetteCode());
        assertNotNull(cree.getLignes().get(0).getId());
    }

    @Test
    void validerPlanPublieLEvenement() {
        PlanProduction plan = planAvecLigne();
        plan.setIdPlan(UUID.randomUUID());
        when(repository.findPlan(any())).thenReturn(Optional.of(plan));
        when(repository.savePlan(any())).thenAnswer(i -> i.getArgument(0));

        service.validerPlan(plan.getIdPlan());

        verify(eventPublisher).publierPlanValide(any(PlanProduction.class));
    }

    @Test
    void creerFourneeGenereLeNumero() {
        Fournee fournee = new Fournee();
        fournee.setRecetteCode("REC-BAG");
        when(repository.countFournees()).thenReturn(0);
        when(repository.saveFournee(any())).thenAnswer(i -> i.getArgument(0));

        Fournee cree = service.creerFournee(fournee);

        assertEquals("FOU-00001", cree.getNumero());
    }

    @Test
    void validerFourneePublieEntreeProductionEtPertes() {
        Fournee fournee = new Fournee();
        fournee.setIdFournee(UUID.randomUUID());
        fournee.setProduitCode("BAGUETTE");
        fournee.setQuantitePrevue(BigDecimal.TEN);
        fournee.lancer();
        fournee.enregistrerRendu(BigDecimal.valueOf(9));
        fournee.cuire();
        PerteProduction perte = new PerteProduction();
        perte.setArticleCode("BAGUETTE");
        perte.setQuantite(BigDecimal.ONE);
        perte.setTypePerte(TypePerte.CASSE);
        perte.valoriser(BigDecimal.ONE);
        fournee.declarerPerte(perte);

        when(repository.findFournee(any())).thenReturn(Optional.of(fournee));
        when(repository.saveFournee(any())).thenAnswer(i -> i.getArgument(0));

        service.validerFournee(fournee.getIdFournee());

        verify(eventPublisher).publierFourneeValidee(any(Fournee.class));
        ArgumentCaptor<MouvementStockIntention> captor = ArgumentCaptor.forClass(MouvementStockIntention.class);
        verify(eventPublisher, times(2)).publierMouvementStock(captor.capture());
        List<TypeMouvementStock> types = captor.getAllValues().stream().map(MouvementStockIntention::type).toList();
        assertTrue(types.contains(TypeMouvementStock.ENTREE_PRODUCTION));
        assertTrue(types.contains(TypeMouvementStock.SORTIE_PERTE));
    }

    @Test
    void declarerConsommationPublieSortieConsommation() {
        Fournee fournee = new Fournee();
        fournee.setIdFournee(UUID.randomUUID());
        when(repository.findFournee(any())).thenReturn(Optional.of(fournee));
        when(repository.saveFournee(any())).thenAnswer(i -> i.getArgument(0));

        LigneConsommation ligne = new LigneConsommation();
        ligne.setArticleCode("FARINE");
        ligne.setLotNumero("LOT-1");
        ligne.setQuantiteReelle(BigDecimal.TEN);

        service.declarerConsommation(fournee.getIdFournee(), List.of(ligne));

        ArgumentCaptor<MouvementStockIntention> captor = ArgumentCaptor.forClass(MouvementStockIntention.class);
        verify(eventPublisher).publierMouvementStock(captor.capture());
        assertEquals(TypeMouvementStock.SORTIE_CONSOMMATION, captor.getValue().type());
        assertEquals("LOT-1", captor.getValue().lotNumero());
    }

    @Test
    void declarerPerteValoriseAuPrixMoyen() {
        Fournee fournee = new Fournee();
        fournee.setIdFournee(UUID.randomUUID());
        when(repository.findFournee(any())).thenReturn(Optional.of(fournee));
        when(repository.saveFournee(any())).thenAnswer(i -> i.getArgument(0));

        PerteProduction perte = new PerteProduction();
        perte.setArticleCode("FARINE");
        perte.setQuantite(BigDecimal.valueOf(3));
        perte.setTypePerte(TypePerte.PATE_PERDUE);

        Fournee resultat = service.declarerPerte(fournee.getIdFournee(), perte, new BigDecimal("2.00"));

        assertEquals(0, resultat.getPertes().get(0).getValorisation().compareTo(new BigDecimal("6.00")));
    }
}
