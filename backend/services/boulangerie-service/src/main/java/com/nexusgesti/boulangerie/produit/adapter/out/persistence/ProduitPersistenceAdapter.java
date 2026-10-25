package com.nexusgesti.boulangerie.produit.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity.*;
import com.nexusgesti.boulangerie.produit.domain.model.*;
import com.nexusgesti.boulangerie.produit.domain.port.out.ProduitRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProduitPersistenceAdapter implements ProduitRepository {

    private final GammeJpaRepository gammeRepository;
    private final CategorieProduitJpaRepository categorieRepository;
    private final ProduitJpaRepository produitRepository;
    private final FicheRecetteJpaRepository recetteRepository;
    private final IngredientRecetteJpaRepository ingredientRepository;
    private final LigneCoutJpaRepository ligneCoutRepository;
    private final ObjectMapper objectMapper;

    public ProduitPersistenceAdapter(GammeJpaRepository gammeRepository,
                                     CategorieProduitJpaRepository categorieRepository,
                                     ProduitJpaRepository produitRepository,
                                     FicheRecetteJpaRepository recetteRepository,
                                     IngredientRecetteJpaRepository ingredientRepository,
                                     LigneCoutJpaRepository ligneCoutRepository,
                                     ObjectMapper objectMapper) {
        this.gammeRepository = gammeRepository;
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
        this.recetteRepository = recetteRepository;
        this.ingredientRepository = ingredientRepository;
        this.ligneCoutRepository = ligneCoutRepository;
        this.objectMapper = objectMapper;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- GAMME ----------

    @Override
    @Transactional
    public Gamme saveGamme(Gamme gamme) {
        if (gamme.getId() == null) {
            gamme.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        GammeEntity entity = new GammeEntity();
        entity.setId(gamme.getId());
        entity.setCode(gamme.getCode());
        entity.setLibelle(gamme.getLibelle());
        entity.setOrdre(gamme.getOrdre() == null ? 0 : gamme.getOrdre());
        entity.setActif(gamme.isActif());
        entity.setTenantId(tenantId);
        return toDomain(gammeRepository.save(entity));
    }

    @Override
    public Optional<Gamme> findGamme(UUID id) {
        return gammeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Gamme> listGammes() {
        List<Gamme> out = new ArrayList<>();
        gammeRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- CATEGORIE ----------

    @Override
    @Transactional
    public CategorieProduit saveCategorie(CategorieProduit categorie) {
        if (categorie.getId() == null) {
            categorie.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        CategorieProduitEntity entity = new CategorieProduitEntity();
        entity.setId(categorie.getId());
        entity.setCode(categorie.getCode());
        entity.setLibelle(categorie.getLibelle());
        entity.setOrdre(categorie.getOrdre() == null ? 0 : categorie.getOrdre());
        entity.setActif(categorie.isActif());
        entity.setTenantId(tenantId);
        return toDomain(categorieRepository.save(entity));
    }

    @Override
    public Optional<CategorieProduit> findCategorie(UUID id) {
        return categorieRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CategorieProduit> listCategories() {
        List<CategorieProduit> out = new ArrayList<>();
        categorieRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- PRODUIT ----------

    @Override
    @Transactional
    public Produit saveProduit(Produit produit) {
        if (produit.getIdProduit() == null) {
            produit.setIdProduit(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ProduitEntity entity = new ProduitEntity();
        entity.setId(produit.getIdProduit());
        entity.setCode(produit.getCode());
        entity.setDesignation(produit.getDesignation());
        entity.setGammeCode(produit.getGammeCode());
        entity.setCategorieCode(produit.getCategorieCode());
        entity.setArticleCode(produit.getArticleCode());
        entity.setUniteVente(produit.getUniteVente() == null ? null : produit.getUniteVente().name());
        entity.setPoidsUnitaireGrammes(produit.getPoidsUnitaireGrammes());
        entity.setPrixVenteHT(produit.getPrixVenteHT());
        entity.setTauxTva(produit.getTauxTva());
        entity.setCodeBarre(produit.getCodeBarre());
        entity.setDlcHeures(produit.getDlcHeures());
        entity.setAllergenes(serializeAllergenes(produit.getAllergenes()));
        entity.setFabricable(produit.isFabricable());
        entity.setVendableAuDetail(produit.isVendableAuDetail());
        entity.setActif(produit.isActif());
        entity.setTenantId(tenantId);
        return toDomain(produitRepository.save(entity));
    }

    @Override
    public Optional<Produit> findProduit(UUID id) {
        return produitRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Produit> listProduits() {
        List<Produit> out = new ArrayList<>();
        produitRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- FICHE RECETTE ----------

    @Override
    @Transactional
    public FicheRecette saveRecette(FicheRecette recette) {
        if (recette.getIdRecette() == null) {
            recette.setIdRecette(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ingredientRepository.deleteByRecetteId(recette.getIdRecette());
        ligneCoutRepository.deleteByRecetteId(recette.getIdRecette());

        FicheRecetteEntity entity = new FicheRecetteEntity();
        entity.setId(recette.getIdRecette());
        entity.setCode(recette.getCode());
        entity.setProduitCode(recette.getProduitCode());
        entity.setLibelle(recette.getLibelle());
        entity.setVersion(recette.getVersion());
        entity.setRendementQuantite(recette.getRendementQuantite());
        entity.setRendementUnite(recette.getRendementUnite() == null ? null : recette.getRendementUnite().name());
        entity.setTempsPreparationMin(recette.getTempsPreparationMin());
        entity.setTempsCuissonMin(recette.getTempsCuissonMin());
        entity.setTemperatureCuissonC(recette.getTemperatureCuissonC());
        entity.setInstructions(recette.getInstructions());
        entity.setActive(recette.isActive());
        entity.setModifiable(recette.isModifiable());
        entity.setTenantId(tenantId);
        recetteRepository.save(entity);

        for (IngredientRecette ingredient : recette.getIngredients()) {
            if (ingredient.getId() == null) {
                ingredient.setId(UUID.randomUUID());
            }
            IngredientRecetteEntity ingredientEntity = new IngredientRecetteEntity();
            ingredientEntity.setId(ingredient.getId());
            ingredientEntity.setRecetteId(recette.getIdRecette());
            ingredientEntity.setArticleCode(ingredient.getArticleCode());
            ingredientEntity.setLibelle(ingredient.getLibelle());
            ingredientEntity.setQuantite(ingredient.getQuantite());
            ingredientEntity.setUnite(ingredient.getUnite());
            ingredientEntity.setTauxPerte(ingredient.getTauxPerte());
            ingredientEntity.setOrdre(ingredient.getOrdre() == null ? 0 : ingredient.getOrdre());
            ingredientEntity.setComptabiliseCout(ingredient.isComptabiliseCout());
            ingredientEntity.setTenantId(tenantId);
            ingredientRepository.save(ingredientEntity);
        }

        for (LigneCout ligne : recette.getLignesCout()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            LigneCoutEntity ligneEntity = new LigneCoutEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setRecetteId(recette.getIdRecette());
            ligneEntity.setNature(ligne.getNature() == null ? null : ligne.getNature().name());
            ligneEntity.setLibelle(ligne.getLibelle());
            ligneEntity.setMontant(ligne.getMontant());
            ligneEntity.setTenantId(tenantId);
            ligneCoutRepository.save(ligneEntity);
        }
        return recette;
    }

    @Override
    public Optional<FicheRecette> findRecette(UUID id) {
        return recetteRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<FicheRecette> findRecetteByCode(String code, Integer version) {
        if (version != null) {
            return recetteRepository.findByCodeAndVersion(code, version).map(this::toDomain);
        }
        return recetteRepository.findFirstByCodeAndActiveTrueOrderByVersionDesc(code).map(this::toDomain);
    }

    @Override
    public List<FicheRecette> listRecettes() {
        List<FicheRecette> out = new ArrayList<>();
        recetteRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    @Override
    public int maxVersionRecette(String code) {
        return recetteRepository.maxVersion(code);
    }

    @Override
    public int countRecettesActives(String produitCode) {
        return recetteRepository.countActives(produitCode);
    }

    // ---------- MAPPING ----------

    private Gamme toDomain(GammeEntity entity) {
        Gamme gamme = new Gamme();
        gamme.setId(entity.getId());
        gamme.setCode(entity.getCode());
        gamme.setLibelle(entity.getLibelle());
        gamme.setOrdre(entity.getOrdre());
        gamme.setActif(entity.isActif());
        return gamme;
    }

    private CategorieProduit toDomain(CategorieProduitEntity entity) {
        CategorieProduit categorie = new CategorieProduit();
        categorie.setId(entity.getId());
        categorie.setCode(entity.getCode());
        categorie.setLibelle(entity.getLibelle());
        categorie.setOrdre(entity.getOrdre());
        categorie.setActif(entity.isActif());
        return categorie;
    }

    private Produit toDomain(ProduitEntity entity) {
        Produit produit = new Produit();
        produit.setIdProduit(entity.getId());
        produit.setCode(entity.getCode());
        produit.setDesignation(entity.getDesignation());
        produit.setGammeCode(entity.getGammeCode());
        produit.setCategorieCode(entity.getCategorieCode());
        produit.setArticleCode(entity.getArticleCode());
        produit.setUniteVente(entity.getUniteVente() == null ? null : UniteVente.valueOf(entity.getUniteVente()));
        produit.setPoidsUnitaireGrammes(entity.getPoidsUnitaireGrammes());
        produit.setPrixVenteHT(entity.getPrixVenteHT());
        produit.setTauxTva(entity.getTauxTva());
        produit.setCodeBarre(entity.getCodeBarre());
        produit.setDlcHeures(entity.getDlcHeures());
        produit.setAllergenes(deserializeAllergenes(entity.getAllergenes()));
        produit.setFabricable(entity.isFabricable());
        produit.setVendableAuDetail(entity.isVendableAuDetail());
        produit.setActif(entity.isActif());
        return produit;
    }

    private FicheRecette toDomain(FicheRecetteEntity entity) {
        FicheRecette recette = new FicheRecette();
        recette.setIdRecette(entity.getId());
        recette.setCode(entity.getCode());
        recette.setProduitCode(entity.getProduitCode());
        recette.setLibelle(entity.getLibelle());
        recette.setVersion(entity.getVersion());
        recette.setRendementQuantite(entity.getRendementQuantite());
        recette.setRendementUnite(entity.getRendementUnite() == null ? null : UniteVente.valueOf(entity.getRendementUnite()));
        recette.setTempsPreparationMin(entity.getTempsPreparationMin());
        recette.setTempsCuissonMin(entity.getTempsCuissonMin());
        recette.setTemperatureCuissonC(entity.getTemperatureCuissonC());
        recette.setInstructions(entity.getInstructions());
        recette.setActive(entity.isActive());
        recette.setModifiable(entity.isModifiable());

        List<IngredientRecette> ingredients = new ArrayList<>();
        for (IngredientRecetteEntity ingredient : ingredientRepository.findByRecetteId(entity.getId())) {
            IngredientRecette model = new IngredientRecette();
            model.setId(ingredient.getId());
            model.setRecetteId(ingredient.getRecetteId());
            model.setArticleCode(ingredient.getArticleCode());
            model.setLibelle(ingredient.getLibelle());
            model.setQuantite(ingredient.getQuantite());
            model.setUnite(ingredient.getUnite());
            model.setTauxPerte(ingredient.getTauxPerte());
            model.setOrdre(ingredient.getOrdre());
            model.setComptabiliseCout(ingredient.isComptabiliseCout());
            ingredients.add(model);
        }
        recette.setIngredients(ingredients);

        List<LigneCout> lignesCout = new ArrayList<>();
        for (LigneCoutEntity ligne : ligneCoutRepository.findByRecetteId(entity.getId())) {
            LigneCout model = new LigneCout();
            model.setId(ligne.getId());
            model.setRecetteId(ligne.getRecetteId());
            model.setNature(ligne.getNature() == null ? null : NatureCout.valueOf(ligne.getNature()));
            model.setLibelle(ligne.getLibelle());
            model.setMontant(ligne.getMontant());
            lignesCout.add(model);
        }
        recette.setLignesCout(lignesCout);
        return recette;
    }

    private String serializeAllergenes(List<Allergene> allergenes) {
        if (allergenes == null || allergenes.isEmpty()) {
            return "[]";
        }
        try {
            List<String> noms = allergenes.stream().map(Enum::name).toList();
            return objectMapper.writeValueAsString(noms);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Serialisation des allergenes impossible", e);
        }
    }

    private List<Allergene> deserializeAllergenes(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            List<String> noms = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return noms.stream().map(Allergene::valueOf).toList();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Deserialisation des allergenes impossible", e);
        }
    }
}
