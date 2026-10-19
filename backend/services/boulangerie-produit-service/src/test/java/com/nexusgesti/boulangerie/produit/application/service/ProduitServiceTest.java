package com.nexusgesti.boulangerie.produit.application.service;

import com.nexusgesti.boulangerie.produit.domain.model.FicheRecette;
import com.nexusgesti.boulangerie.produit.domain.model.Produit;
import com.nexusgesti.boulangerie.produit.domain.model.UniteVente;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitEventPublisher;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProduitServiceTest {

    private ProduitRepository repository;
    private ProduitEventPublisher eventPublisher;
    private ProduitService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProduitRepository.class);
        eventPublisher = mock(ProduitEventPublisher.class);
        service = new ProduitService(repository, eventPublisher);
    }

    private FicheRecette recette(UUID id, String code, String produitCode) {
        FicheRecette recette = new FicheRecette();
        recette.setIdRecette(id);
        recette.setCode(code);
        recette.setProduitCode(produitCode);
        recette.setLibelle("Baguette");
        recette.setVersion(1);
        return recette;
    }

    @Test
    void premiereVersionEstUne() {
        FicheRecette recette = recette(null, "REC-BAG", "BAGUETTE");
        when(repository.maxVersionRecette("REC-BAG")).thenReturn(0);
        when(repository.saveRecette(any())).thenAnswer(i -> i.getArgument(0));

        FicheRecette cree = service.creerRecette(recette);
        assertEquals(1, cree.getVersion());
    }

    @Test
    void correctionApresFigerCreeUneNouvelleVersion() {
        FicheRecette recette = recette(null, "REC-BAG", "BAGUETTE");
        when(repository.maxVersionRecette("REC-BAG")).thenReturn(3);
        when(repository.saveRecette(any())).thenAnswer(i -> i.getArgument(0));

        FicheRecette cree = service.creerRecette(recette);
        assertEquals(4, cree.getVersion());
    }

    @Test
    void activerRecetteMetAJourLeDrapeauFabricable() {
        UUID recetteId = UUID.randomUUID();
        FicheRecette recette = recette(recetteId, "REC-BAG", "BAGUETTE");
        Produit produit = new Produit();
        produit.setIdProduit(UUID.randomUUID());
        produit.setCode("BAGUETTE");
        produit.setUniteVente(UniteVente.PIECE);

        when(repository.findRecette(recetteId)).thenReturn(Optional.of(recette));
        when(repository.saveRecette(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.countRecettesActives("BAGUETTE")).thenReturn(1);
        when(repository.listProduits()).thenReturn(List.of(produit));
        when(repository.saveProduit(any())).thenAnswer(i -> i.getArgument(0));

        FicheRecette activee = service.activerRecette(recetteId);

        assertTrue(activee.isActive());
        assertTrue(produit.isFabricable());
        verify(eventPublisher).publierRecetteActivee(activee);
    }

    @Test
    void figerRecettePublieUnEvenement() {
        UUID recetteId = UUID.randomUUID();
        FicheRecette recette = recette(recetteId, "REC-BAG", "BAGUETTE");
        when(repository.findRecette(recetteId)).thenReturn(Optional.of(recette));
        when(repository.saveRecette(any())).thenAnswer(i -> i.getArgument(0));

        FicheRecette figee = service.figerRecette(recetteId);

        assertTrue(figee.estFigee());
        verify(eventPublisher).publierRecetteFigee(figee);
    }

    @Test
    void creerProduitPublieLEvenementProduitCree() {
        Produit produit = new Produit();
        produit.setCode("BAGUETTE");
        produit.setUniteVente(UniteVente.PIECE);
        when(repository.saveProduit(any())).thenAnswer(i -> {
            Produit p = i.getArgument(0);
            p.setIdProduit(UUID.randomUUID());
            return p;
        });

        Produit cree = service.creerProduit(produit);

        assertNotNull(cree.getIdProduit());
        verify(eventPublisher).publierProduitCree(cree);
    }
}
