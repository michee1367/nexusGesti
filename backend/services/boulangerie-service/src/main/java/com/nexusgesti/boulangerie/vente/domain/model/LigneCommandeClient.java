package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.util.UUID;

public class LigneCommandeClient {

    private UUID id;
    private String produitCode;
    private String designation;
    private BigDecimal quantite = BigDecimal.ZERO;
    private BigDecimal prixUnitaire = BigDecimal.ZERO;
    private String personnalisation;

    public LigneCommandeClient() {
    }

    public LigneCommandeClient(String produitCode, String designation,
                               BigDecimal quantite, BigDecimal prixUnitaire, String personnalisation) {
        this.id = UUID.randomUUID();
        this.produitCode = produitCode;
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.personnalisation = personnalisation;
    }

    public void verifier() {
        if (produitCode == null || produitCode.isBlank()) {
            throw VenteException.invalide("Le produit est obligatoire sur une ligne de commande");
        }
        if (designation == null || designation.isBlank()) {
            throw VenteException.invalide("La designation est obligatoire sur une ligne de commande");
        }
        if (quantite == null || quantite.signum() <= 0) {
            throw VenteException.invalide("La quantite doit etre strictement positive");
        }
        if (prixUnitaire == null || prixUnitaire.signum() < 0) {
            throw VenteException.invalide("Le prix unitaire ne peut pas etre negatif");
        }
    }

    public BigDecimal getMontant() {
        if (quantite == null || prixUnitaire == null) {
            return BigDecimal.ZERO;
        }
        return quantite.multiply(prixUnitaire);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduitCode() {
        return produitCode;
    }

    public void setProduitCode(String produitCode) {
        this.produitCode = produitCode;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getPersonnalisation() {
        return personnalisation;
    }

    public void setPersonnalisation(String personnalisation) {
        this.personnalisation = personnalisation;
    }
}
