package com.nexusgesti.boulangerie.produit.domain.port.out;

import com.nexusgesti.boulangerie.produit.domain.model.FicheRecette;
import com.nexusgesti.boulangerie.produit.domain.model.Produit;

public interface ProduitEventPublisher {

    void publierProduitCree(Produit produit);

    void publierRecetteActivee(FicheRecette recette);

    void publierRecetteFigee(FicheRecette recette);
}
