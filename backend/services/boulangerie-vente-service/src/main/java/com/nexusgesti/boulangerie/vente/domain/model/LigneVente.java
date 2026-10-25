package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class LigneVente {

    private UUID id;
    private String produitCode;
    private String designation;
    private BigDecimal quantite = BigDecimal.ZERO;
    private BigDecimal prixUnitaireHT = BigDecimal.ZERO;
    private BigDecimal tauxTva = BigDecimal.ZERO;
    private BigDecimal remisePct = BigDecimal.ZERO;
    private BigDecimal montantHT = BigDecimal.ZERO;
    private BigDecimal montantTVA = BigDecimal.ZERO;
    private String lotNumero;

    public LigneVente() {
    }

    public LigneVente(String produitCode, String designation, BigDecimal quantite,
                      BigDecimal prixUnitaireHT, BigDecimal tauxTva, BigDecimal remisePct,
                      String lotNumero) {
        this.id = UUID.randomUUID();
        this.produitCode = produitCode;
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTva = tauxTva;
        this.remisePct = remisePct;
        this.lotNumero = lotNumero;
    }

    public void verifier() {
        if (produitCode == null || produitCode.isBlank()) {
            throw VenteException.invalide("Le produit est obligatoire sur une ligne de vente");
        }
        if (designation == null || designation.isBlank()) {
            throw VenteException.invalide("La designation est obligatoire sur une ligne de vente");
        }
        if (quantite == null || quantite.signum() <= 0) {
            throw VenteException.invalide("La quantite doit etre strictement positive");
        }
        if (prixUnitaireHT == null || prixUnitaireHT.signum() < 0) {
            throw VenteException.invalide("Le prix unitaire ne peut pas etre negatif");
        }
        if (tauxTva == null || tauxTva.signum() < 0) {
            throw VenteException.invalide("Le taux de TVA ne peut pas etre negatif");
        }
        if (remisePct != null && (remisePct.signum() < 0 || remisePct.compareTo(new BigDecimal("100")) > 0)) {
            throw VenteException.invalide("La remise doit etre comprise entre 0 et 100");
        }
    }

    public void calculerMontants() {
        BigDecimal brut = quantite.multiply(prixUnitaireHT);
        BigDecimal taux = remisePct == null ? BigDecimal.ZERO : remisePct;
        BigDecimal net = brut.multiply(BigDecimal.ONE.subtract(taux.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)));
        this.montantHT = net.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tva = tauxTva == null ? BigDecimal.ZERO : tauxTva;
        this.montantTVA = net.multiply(tva.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getPrixUnitaireHT() {
        return prixUnitaireHT;
    }

    public void setPrixUnitaireHT(BigDecimal prixUnitaireHT) {
        this.prixUnitaireHT = prixUnitaireHT;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }

    public BigDecimal getRemisePct() {
        return remisePct;
    }

    public void setRemisePct(BigDecimal remisePct) {
        this.remisePct = remisePct;
    }

    public BigDecimal getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(BigDecimal montantHT) {
        this.montantHT = montantHT;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(BigDecimal montantTVA) {
        this.montantTVA = montantTVA;
    }

    public String getLotNumero() {
        return lotNumero;
    }

    public void setLotNumero(String lotNumero) {
        this.lotNumero = lotNumero;
    }
}
