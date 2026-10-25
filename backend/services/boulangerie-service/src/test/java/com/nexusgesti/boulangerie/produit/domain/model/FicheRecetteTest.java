package com.nexusgesti.boulangerie.produit.domain.model;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FicheRecetteTest {

    private FicheRecette recetteRendement(BigDecimal rendement) {
        FicheRecette recette = new FicheRecette();
        recette.setIdRecette(java.util.UUID.randomUUID());
        recette.setCode("REC-BAGUETTE");
        recette.setRendementQuantite(rendement);
        recette.setRendementUnite(UniteVente.PIECE);
        return recette;
    }

    private IngredientRecette ingredient(String articleCode, BigDecimal quantite, BigDecimal tauxPerte) {
        IngredientRecette ingredient = new IngredientRecette();
        ingredient.setArticleCode(articleCode);
        ingredient.setLibelle("Farine T55");
        ingredient.setQuantite(quantite);
        ingredient.setUnite("KG");
        ingredient.setTauxPerte(tauxPerte);
        return ingredient;
    }

    @Test
    void activerEtFigerSuccedent() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.activer();
        assertTrue(recette.isActive());
        recette.figer();
        assertTrue(recette.estFigee());
        assertFalse(recette.isModifiable());
    }

    @Test
    void figerDeuxFoisEstRefuse() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.figer();
        assertThrows(ProduitException.class, recette::figer);
    }

    @Test
    void ajouterIngredientSurRecetteFigeeEstRefuse() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.figer();
        assertThrows(ProduitException.class,
                () -> recette.ajouterIngredient(ingredient("FARINE", BigDecimal.ONE, BigDecimal.ZERO)));
    }

    @Test
    void ajouterIngredientPoseLIdEtLeRecetteId() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        IngredientRecette ingredient = ingredient("FARINE", BigDecimal.ONE, BigDecimal.ZERO);
        recette.ajouterIngredient(ingredient);
        assertNotNull(ingredient.getId());
        assertEquals(recette.getIdRecette(), ingredient.getRecetteId());
    }

    @Test
    void quantiteBruteAppliqueLeTauxDePerte() {
        IngredientRecette ingredient = ingredient("FARINE", new BigDecimal("10"), new BigDecimal("0.05"));
        assertEquals(new BigDecimal("10.5000"), ingredient.getQuantiteBrute());
    }

    @Test
    void coutMatiereUtiliseLesPrixMoyensFournis() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.ajouterIngredient(ingredient("FARINE", new BigDecimal("10"), new BigDecimal("0.05")));
        Map<String, BigDecimal> prix = Map.of("FARINE", new BigDecimal("2.00"));
        assertEquals(new BigDecimal("21.00"), recette.getCoutMatiere(prix));
    }

    @Test
    void coutMatiereSansPrixRenvoieZero() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.ajouterIngredient(ingredient("FARINE", new BigDecimal("10"), BigDecimal.ZERO));
        assertEquals(0, recette.getCoutMatiere(Map.of()).signum());
    }

    @Test
    void coutRevientAjouteLesLignesDeCout() {
        FicheRecette recette = recetteRendement(BigDecimal.TEN);
        recette.ajouterIngredient(ingredient("FARINE", new BigDecimal("10"), BigDecimal.ZERO));
        LigneCout main = new LigneCout();
        main.setNature(NatureCout.MAIN_OEUVRE);
        main.setLibelle("Four");
        main.setMontant(new BigDecimal("5.00"));
        recette.ajouterLigneCout(main);
        Map<String, BigDecimal> prix = Map.of("FARINE", new BigDecimal("2.00"));
        assertEquals(new BigDecimal("25.00"), recette.getCoutRevient(prix));
    }

    @Test
    void besoinsMatiereProportionnelsAuRendement() {
        FicheRecette recette = recetteRendement(new BigDecimal("10"));
        recette.ajouterIngredient(ingredient("FARINE", new BigDecimal("5"), new BigDecimal("0.10")));
        List<BesoinMatiere> besoins = recette.getBesoinsMatiere(new BigDecimal("20"));
        assertEquals(1, besoins.size());
        assertEquals(new BigDecimal("10.0000"), besoins.get(0).quantiteNette());
        assertEquals(new BigDecimal("11.0000"), besoins.get(0).quantiteBrute());
    }

    @Test
    void besoinsAvecRendementNulEstRefuse() {
        FicheRecette recette = recetteRendement(BigDecimal.ZERO);
        assertThrows(ProduitException.class, () -> recette.getBesoinsMatiere(BigDecimal.TEN));
    }
}
