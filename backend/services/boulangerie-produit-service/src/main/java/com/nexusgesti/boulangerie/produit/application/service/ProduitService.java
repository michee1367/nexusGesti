package com.nexusgesti.boulangerie.produit.application.service;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;
import com.nexusgesti.boulangerie.produit.domain.model.*;
import com.nexusgesti.boulangerie.produit.domain.port.in.ProduitUseCase;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitEventPublisher;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ProduitService implements ProduitUseCase {

    private final ProduitRepository repository;
    private final ProduitEventPublisher eventPublisher;

    public ProduitService(ProduitRepository repository, ProduitEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    // ---------- GAMME / CATEGORIE ----------

    @Override
    public Gamme creerGamme(Gamme gamme) {
        return repository.saveGamme(gamme);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gamme> listGammes() {
        return repository.listGammes();
    }

    @Override
    public CategorieProduit creerCategorie(CategorieProduit categorie) {
        return repository.saveCategorie(categorie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieProduit> listCategories() {
        return repository.listCategories();
    }

    // ---------- PRODUIT ----------

    @Override
    public Produit creerProduit(Produit produit) {
        Produit cree = repository.saveProduit(produit);
        eventPublisher.publierProduitCree(cree);
        return cree;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> listProduits(String gammeCode, String categorieCode) {
        return repository.listProduits().stream()
                .filter(p -> gammeCode == null || gammeCode.isBlank() || gammeCode.equals(p.getGammeCode()))
                .filter(p -> categorieCode == null || categorieCode.isBlank() || categorieCode.equals(p.getCategorieCode()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Produit getProduit(UUID id) {
        return loadProduit(id);
    }

    @Override
    public Produit activerProduit(UUID id) {
        Produit produit = loadProduit(id);
        produit.activer();
        return repository.saveProduit(produit);
    }

    @Override
    public Produit desactiverProduit(UUID id) {
        Produit produit = loadProduit(id);
        produit.desactiver();
        return repository.saveProduit(produit);
    }

    // ---------- FICHE RECETTE ----------

    @Override
    public FicheRecette creerRecette(FicheRecette recette) {
        if (recette.getCode() == null || recette.getCode().isBlank()) {
            throw ProduitException.invalide("Code de recette obligatoire");
        }
        recette.setVersion(repository.maxVersionRecette(recette.getCode()) + 1);
        FicheRecette cree = repository.saveRecette(recette);
        majFabricable(cree.getProduitCode());
        return cree;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FicheRecette> listRecettes(String produitCode) {
        return repository.listRecettes().stream()
                .filter(r -> produitCode == null || produitCode.isBlank() || produitCode.equals(r.getProduitCode()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FicheRecette getRecette(UUID id) {
        return loadRecette(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FicheRecette getRecetteByCode(String code, Integer version) {
        return loadRecetteByCode(code, version);
    }

    @Override
    public FicheRecette ajouterIngredient(UUID recetteId, IngredientRecette ingredient) {
        FicheRecette recette = loadRecette(recetteId);
        recette.ajouterIngredient(ingredient);
        return repository.saveRecette(recette);
    }

    @Override
    public FicheRecette activerRecette(UUID id) {
        FicheRecette recette = loadRecette(id);
        recette.activer();
        FicheRecette activee = repository.saveRecette(recette);
        majFabricable(activee.getProduitCode());
        eventPublisher.publierRecetteActivee(activee);
        return activee;
    }

    @Override
    public FicheRecette figerRecette(UUID id) {
        FicheRecette recette = loadRecette(id);
        recette.figer();
        FicheRecette figee = repository.saveRecette(recette);
        eventPublisher.publierRecetteFigee(figee);
        return figee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BesoinMatiere> getBesoins(UUID recetteId, BigDecimal quantite) {
        return loadRecette(recetteId).getBesoinsMatiere(quantite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BesoinMatiere> getBesoinsByCode(String code, Integer version, BigDecimal quantite) {
        return loadRecetteByCode(code, version).getBesoinsMatiere(quantite);
    }

    @Override
    @Transactional(readOnly = true)
    public CoutRecette getCout(UUID recetteId, Map<String, BigDecimal> prixMoyens) {
        FicheRecette recette = loadRecette(recetteId);
        return new CoutRecette(
                recette.getCoutMatiere(prixMoyens),
                recette.getCoutRevient(prixMoyens),
                !prixMoyens.isEmpty());
    }

    // ---------- PRIVE ----------

    private Produit loadProduit(UUID id) {
        return repository.findProduit(id)
                .orElseThrow(() -> ProduitException.notFound("Produit", id));
    }

    private FicheRecette loadRecette(UUID id) {
        return repository.findRecette(id)
                .orElseThrow(() -> ProduitException.notFound("FicheRecette", id));
    }

    private FicheRecette loadRecetteByCode(String code, Integer version) {
        return repository.findRecetteByCode(code, version)
                .orElseThrow(() -> ProduitException.notFoundByCode("FicheRecette", code));
    }

    private void majFabricable(String produitCode) {
        if (produitCode == null || produitCode.isBlank()) {
            return;
        }
        boolean fabricable = repository.countRecettesActives(produitCode) > 0;
        for (Produit produit : repository.listProduits()) {
            if (produitCode.equals(produit.getCode()) && produit.isFabricable() != fabricable) {
                produit.setFabricable(fabricable);
                repository.saveProduit(produit);
            }
        }
    }
}
