package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;

import java.math.BigDecimal;
import java.util.UUID;

public class FournisseurBoulangerie {

    private UUID idFournisseur;
    private String code;
    private String tiersCode;
    private String raisonSociale;
    private Integer delaiLivraisonJours = 0;
    private String conditionsReglement;
    private String categorieProduits;
    private BigDecimal montantMiniCommande;
    private boolean actif = true;

    public FournisseurBoulangerie desactiver() {
        this.actif = false;
        return this;
    }

    public FournisseurBoulangerie reactiver() {
        this.actif = true;
        return this;
    }

    public boolean estFiable() {
        return actif && delaiLivraisonJours != null;
    }

    public int getDelai() {
        return delaiLivraisonJours == null ? 0 : delaiLivraisonJours;
    }

    public void verifierModifiable() {
        if (code == null || code.isBlank()) {
            throw ApprovisionnementException.invalide("Le code fournisseur est obligatoire");
        }
        if (raisonSociale == null || raisonSociale.isBlank()) {
            throw ApprovisionnementException.invalide("La raison sociale est obligatoire");
        }
        if (delaiLivraisonJours != null && delaiLivraisonJours < 0) {
            throw ApprovisionnementException.invalide("Le delai de livraison ne peut pas etre negatif");
        }
    }

    public UUID getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(UUID idFournisseur) { this.idFournisseur = idFournisseur; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTiersCode() { return tiersCode; }
    public void setTiersCode(String tiersCode) { this.tiersCode = tiersCode; }
    public String getRaisonSociale() { return raisonSociale; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public Integer getDelaiLivraisonJours() { return delaiLivraisonJours; }
    public void setDelaiLivraisonJours(Integer delaiLivraisonJours) { this.delaiLivraisonJours = delaiLivraisonJours; }
    public String getConditionsReglement() { return conditionsReglement; }
    public void setConditionsReglement(String conditionsReglement) { this.conditionsReglement = conditionsReglement; }
    public String getCategorieProduits() { return categorieProduits; }
    public void setCategorieProduits(String categorieProduits) { this.categorieProduits = categorieProduits; }
    public BigDecimal getMontantMiniCommande() { return montantMiniCommande; }
    public void setMontantMiniCommande(BigDecimal montantMiniCommande) { this.montantMiniCommande = montantMiniCommande; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
