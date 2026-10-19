package com.nexusgesti.boulangerie.produit.domain.model;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Produit {

    private UUID idProduit;
    private String code;
    private String designation;
    private String gammeCode;
    private String categorieCode;
    private String articleCode;
    private UniteVente uniteVente;
    private Integer poidsUnitaireGrammes;
    private BigDecimal prixVenteHT = BigDecimal.ZERO;
    private BigDecimal tauxTva = BigDecimal.ZERO;
    private String codeBarre;
    private Integer dlcHeures;
    private List<Allergene> allergenes = new ArrayList<>();
    private boolean fabricable;
    private boolean vendableAuDetail = true;
    private boolean actif = true;

    public boolean estFabricable() {
        return fabricable;
    }

    public boolean contientAllergene(Allergene allergene) {
        return allergenes.contains(allergene);
    }

    public BigDecimal getPrixTTC() {
        BigDecimal coefficient = BigDecimal.ONE.add(tauxTva.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        return prixVenteHT.multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
    }

    public void activer() {
        if (actif) {
            throw ProduitException.transitionInvalide("INACTIF", "ACTIF");
        }
        this.actif = true;
    }

    public void desactiver() {
        if (!actif) {
            throw ProduitException.transitionInvalide("ACTIF", "INACTIF");
        }
        this.actif = false;
    }

    public UUID getIdProduit() { return idProduit; }
    public void setIdProduit(UUID idProduit) { this.idProduit = idProduit; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getGammeCode() { return gammeCode; }
    public void setGammeCode(String gammeCode) { this.gammeCode = gammeCode; }
    public String getCategorieCode() { return categorieCode; }
    public void setCategorieCode(String categorieCode) { this.categorieCode = categorieCode; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public UniteVente getUniteVente() { return uniteVente; }
    public void setUniteVente(UniteVente uniteVente) { this.uniteVente = uniteVente; }
    public Integer getPoidsUnitaireGrammes() { return poidsUnitaireGrammes; }
    public void setPoidsUnitaireGrammes(Integer poidsUnitaireGrammes) { this.poidsUnitaireGrammes = poidsUnitaireGrammes; }
    public BigDecimal getPrixVenteHT() { return prixVenteHT; }
    public void setPrixVenteHT(BigDecimal prixVenteHT) { this.prixVenteHT = prixVenteHT; }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public Integer getDlcHeures() { return dlcHeures; }
    public void setDlcHeures(Integer dlcHeures) { this.dlcHeures = dlcHeures; }
    public boolean isFabricable() { return fabricable; }
    public void setFabricable(boolean fabricable) { this.fabricable = fabricable; }
    public boolean isVendableAuDetail() { return vendableAuDetail; }
    public void setVendableAuDetail(boolean vendableAuDetail) { this.vendableAuDetail = vendableAuDetail; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public List<Allergene> getAllergenes() { return allergenes; }
    public void setAllergenes(List<Allergene> allergenes) {
        this.allergenes = allergenes == null ? new ArrayList<>() : new ArrayList<>(allergenes);
    }
}
