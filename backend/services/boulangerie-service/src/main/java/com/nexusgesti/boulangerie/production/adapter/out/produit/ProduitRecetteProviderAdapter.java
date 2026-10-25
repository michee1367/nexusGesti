package com.nexusgesti.boulangerie.production.adapter.out.produit;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;
import com.nexusgesti.boulangerie.produit.domain.port.in.ProduitUseCase;
import com.nexusgesti.boulangerie.production.domain.ProductionException;
import com.nexusgesti.boulangerie.production.domain.model.BesoinMatiere;
import com.nexusgesti.boulangerie.production.domain.port.out.RecetteProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProduitRecetteProviderAdapter implements RecetteProvider {

    private final ProduitUseCase produitUseCase;

    public ProduitRecetteProviderAdapter(ProduitUseCase produitUseCase) {
        this.produitUseCase = produitUseCase;
    }

    @Override
    public List<BesoinMatiere> resoudreBesoins(String recetteCode, Integer versionRecette, BigDecimal quantite) {
        try {
            return produitUseCase.getBesoinsByCode(recetteCode, versionRecette, quantite).stream()
                    .map(b -> new BesoinMatiere(b.articleCode(), b.libelle(), b.quantiteNette(),
                            b.quantiteBrute(), b.unite()))
                    .toList();
        } catch (ProduitException e) {
            throw ProductionException.invalide(
                    "Recette indisponible : " + recetteCode + " (" + e.getMessage() + ")");
        }
    }
}
