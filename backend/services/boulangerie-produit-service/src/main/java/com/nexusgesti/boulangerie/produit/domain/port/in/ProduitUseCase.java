package com.nexusgesti.boulangerie.produit.domain.port.in;

import com.nexusgesti.boulangerie.produit.domain.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProduitUseCase {

    Gamme creerGamme(Gamme gamme);
    List<Gamme> listGammes();

    CategorieProduit creerCategorie(CategorieProduit categorie);
    List<CategorieProduit> listCategories();

    Produit creerProduit(Produit produit);
    List<Produit> listProduits(String gammeCode, String categorieCode);
    Produit getProduit(UUID id);
    Produit activerProduit(UUID id);
    Produit desactiverProduit(UUID id);

    FicheRecette creerRecette(FicheRecette recette);
    List<FicheRecette> listRecettes(String produitCode);
    FicheRecette getRecette(UUID id);
    FicheRecette getRecetteByCode(String code, Integer version);
    FicheRecette ajouterIngredient(UUID recetteId, IngredientRecette ingredient);
    FicheRecette activerRecette(UUID id);
    FicheRecette figerRecette(UUID id);

    List<BesoinMatiere> getBesoins(UUID recetteId, BigDecimal quantite);
    List<BesoinMatiere> getBesoinsByCode(String code, Integer version, BigDecimal quantite);
    CoutRecette getCout(UUID recetteId, Map<String, BigDecimal> prixMoyens);
}
