package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GrilleTarifaireTest {

    @Test
    void activerPuisFigerPuisCorrigerEstInterdit() {
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.activer();
        grille.figer();
        assertTrue(grille.estFigee());
        assertThrows(FeesException.class, grille::activer);
        assertThrows(FeesException.class, grille::verifierModifiable);
    }

    @Test
    void figerDeuxFoisEstRefuse() {
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.figer();
        assertThrows(FeesException.class, grille::figer);
    }

    @Test
    void archiverPositionneStatutEtActive() {
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.activer();
        grille.archiver();
        assertEquals(StatutGrille.ARCHIVEE, grille.getStatut());
        assertFalse(grille.isActive());
    }

    @Test
    void getCoutTotalFiltreParTypeFrais() {
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setId(UUID.randomUUID());
        grille.ajouterLigne(ligne("SCOLARITE", "100.00"));
        grille.ajouterLigne(ligne("CANTINE", "50.00"));
        assertEquals(new BigDecimal("100.00"), grille.getCoutTotal("SCOLARITE", null));
        assertEquals(new BigDecimal("150.00"), grille.getCoutTotal(null, null));
    }

    @Test
    void trouverReductionInconnueLeve404() {
        GrilleTarifaire grille = new GrilleTarifaire();
        assertThrows(FeesException.class, () -> grille.trouverReduction(UUID.randomUUID()));
    }

    private LigneGrille ligne() {
        return ligne("SCOLARITE", "100.00");
    }

    private LigneGrille ligne(String typeFrais, String montant) {
        LigneGrille ligne = new LigneGrille();
        ligne.setTypeFrais(TypeFrais.valueOf(typeFrais));
        ligne.setMontant(new BigDecimal(montant));
        return ligne;
    }
}