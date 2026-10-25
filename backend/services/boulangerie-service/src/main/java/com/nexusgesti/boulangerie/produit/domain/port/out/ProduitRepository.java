package com.nexusgesti.boulangerie.produit.domain.port.out;

import com.nexusgesti.boulangerie.produit.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProduitRepository {

    Gamme saveGamme(Gamme gamme);
    Optional<Gamme> findGamme(UUID id);
    List<Gamme> listGammes();

    CategorieProduit saveCategorie(CategorieProduit categorie);
    Optional<CategorieProduit> findCategorie(UUID id);
    List<CategorieProduit> listCategories();

    Produit saveProduit(Produit produit);
    Optional<Produit> findProduit(UUID id);
    List<Produit> listProduits();

    FicheRecette saveRecette(FicheRecette recette);
    Optional<FicheRecette> findRecette(UUID id);
    Optional<FicheRecette> findRecetteByCode(String code, Integer version);
    List<FicheRecette> listRecettes();
    int maxVersionRecette(String code);
    int countRecettesActives(String produitCode);
}
