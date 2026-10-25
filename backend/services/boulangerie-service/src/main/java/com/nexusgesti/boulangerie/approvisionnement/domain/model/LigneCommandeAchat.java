package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class LigneCommandeAchat {

    private UUID id;
    private UUID commandeId;
    private String articleCode;
    private String designation;
    private BigDecimal quantiteCommandee = BigDecimal.ZERO;
    private BigDecimal quantiteRecue = BigDecimal.ZERO;
    private BigDecimal prixUnitaire = BigDecimal.ZERO;
    private BigDecimal tauxTva = BigDecimal.ZERO;

    public BigDecimal getMontantHT() {
        BigDecimal qte = quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee;
        BigDecimal prix = prixUnitaire == null ? BigDecimal.ZERO : prixUnitaire;
        return qte.multiply(prix).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMontantTVA() {
        BigDecimal ht = getMontantHT();
        BigDecimal taux = tauxTva == null ? BigDecimal.ZERO : tauxTva;
        return ht.multiply(taux).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getResteARecevoir() {
        BigDecimal commandee = quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee;
        BigDecimal recue = quantiteRecue == null ? BigDecimal.ZERO : quantiteRecue;
        BigDecimal reste = commandee.subtract(recue);
        return reste.signum() < 0 ? BigDecimal.ZERO : reste;
    }

    public void verifier() {
        if (articleCode == null || articleCode.isBlank()) {
            throw ApprovisionnementException.invalide("L article est obligatoire sur une ligne de commande");
        }
        if (designation == null || designation.isBlank()) {
            throw ApprovisionnementException.invalide("La designation est obligatoire sur une ligne de commande");
        }
        if (quantiteCommandee == null || quantiteCommandee.signum() <= 0) {
            throw ApprovisionnementException.invalide("La quantite commandee doit etre superieure a zero");
        }
        if (prixUnitaire == null || prixUnitaire.signum() < 0) {
            throw ApprovisionnementException.invalide("Le prix unitaire ne peut pas etre negatif");
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCommandeId() { return commandeId; }
    public void setCommandeId(UUID commandeId) { this.commandeId = commandeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(BigDecimal quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee;
    }
    public BigDecimal getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue == null ? BigDecimal.ZERO : quantiteRecue;
    }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire == null ? BigDecimal.ZERO : prixUnitaire;
    }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva == null ? BigDecimal.ZERO : tauxTva;
    }
}
