package com.nexusgesti.boulangerie.produit.domain.model;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FicheRecette {

    private UUID idRecette;
    private String code;
    private String produitCode;
    private String libelle;
    private Integer version = 1;
    private BigDecimal rendementQuantite = BigDecimal.ZERO;
    private UniteVente rendementUnite;
    private Integer tempsPreparationMin = 0;
    private Integer tempsCuissonMin = 0;
    private Integer temperatureCuissonC;
    private String instructions;
    private boolean active;
    private boolean modifiable = true;

    private List<IngredientRecette> ingredients = new ArrayList<>();
    private List<LigneCout> lignesCout = new ArrayList<>();

    public FicheRecette activer() {
        if (estFigee()) {
            throw ProduitException.transitionInvalide("FIGEE", "ACTIVER");
        }
        this.active = true;
        return this;
    }

    public FicheRecette figer() {
        if (estFigee()) {
            throw ProduitException.transitionInvalide("FIGEE", "FIGER");
        }
        this.modifiable = false;
        return this;
    }

    public boolean estFigee() {
        return !modifiable;
    }

    public void verifierModifiable() {
        if (estFigee()) {
            throw ProduitException.etatInvalide("Recette figee : toute correction cree une nouvelle version");
        }
    }

    public FicheRecette ajouterIngredient(IngredientRecette ingredient) {
        verifierModifiable();
        if (ingredient.getId() == null) {
            ingredient.setId(UUID.randomUUID());
        }
        ingredient.setRecetteId(idRecette);
        this.ingredients.add(ingredient);
        return this;
    }

    public FicheRecette ajouterLigneCout(LigneCout ligne) {
        verifierModifiable();
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        ligne.setRecetteId(idRecette);
        this.lignesCout.add(ligne);
        return this;
    }

    public BigDecimal getCoutMatiere(Map<String, BigDecimal> prixMoyens) {
        BigDecimal total = ingredients.stream()
                .filter(IngredientRecette::isComptabiliseCout)
                .map(i -> i.getQuantiteBrute()
                        .multiply(prixMoyens.getOrDefault(i.getArticleCode(), BigDecimal.ZERO)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCoutRevient(Map<String, BigDecimal> prixMoyens) {
        BigDecimal autres = lignesCout.stream()
                .map(LigneCout::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return getCoutMatiere(prixMoyens).add(autres).setScale(2, RoundingMode.HALF_UP);
    }

    public List<BesoinMatiere> getBesoinsMatiere(BigDecimal quantite) {
        if (rendementQuantite == null || rendementQuantite.signum() == 0) {
            throw ProduitException.invalide("Rendement nul : besoins non calculables");
        }
        if (quantite == null || quantite.signum() < 0) {
            throw ProduitException.invalide("Quantite demandee invalide");
        }
        BigDecimal ratio = quantite.divide(rendementQuantite, 10, RoundingMode.HALF_UP);
        List<BesoinMatiere> besoins = new ArrayList<>();
        for (IngredientRecette ingredient : ingredients) {
            besoins.add(new BesoinMatiere(
                    ingredient.getArticleCode(),
                    ingredient.getLibelle(),
                    ingredient.getQuantiteNette().multiply(ratio).setScale(4, RoundingMode.HALF_UP),
                    ingredient.getQuantiteBrute().multiply(ratio).setScale(4, RoundingMode.HALF_UP),
                    ingredient.getUnite()));
        }
        return besoins;
    }

    public UUID getIdRecette() { return idRecette; }
    public void setIdRecette(UUID idRecette) { this.idRecette = idRecette; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public BigDecimal getRendementQuantite() { return rendementQuantite; }
    public void setRendementQuantite(BigDecimal rendementQuantite) { this.rendementQuantite = rendementQuantite; }
    public UniteVente getRendementUnite() { return rendementUnite; }
    public void setRendementUnite(UniteVente rendementUnite) { this.rendementUnite = rendementUnite; }
    public Integer getTempsPreparationMin() { return tempsPreparationMin; }
    public void setTempsPreparationMin(Integer tempsPreparationMin) { this.tempsPreparationMin = tempsPreparationMin; }
    public Integer getTempsCuissonMin() { return tempsCuissonMin; }
    public void setTempsCuissonMin(Integer tempsCuissonMin) { this.tempsCuissonMin = tempsCuissonMin; }
    public Integer getTemperatureCuissonC() { return temperatureCuissonC; }
    public void setTemperatureCuissonC(Integer temperatureCuissonC) { this.temperatureCuissonC = temperatureCuissonC; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public List<IngredientRecette> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientRecette> ingredients) {
        this.ingredients = ingredients == null ? new ArrayList<>() : new ArrayList<>(ingredients);
    }
    public List<LigneCout> getLignesCout() { return lignesCout; }
    public void setLignesCout(List<LigneCout> lignesCout) {
        this.lignesCout = lignesCout == null ? new ArrayList<>() : new ArrayList<>(lignesCout);
    }
}
