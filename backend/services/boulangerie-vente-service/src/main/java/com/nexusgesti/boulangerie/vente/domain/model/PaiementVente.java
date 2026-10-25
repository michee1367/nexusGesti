package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class PaiementVente {

    private UUID id;
    private ModePaiementCaisse mode;
    private BigDecimal montant = BigDecimal.ZERO;
    private String devise = "USD";
    private BigDecimal montantRecu = BigDecimal.ZERO;
    private BigDecimal rendu = BigDecimal.ZERO;
    private String referenceExterne;

    public PaiementVente() {
    }

    public PaiementVente(ModePaiementCaisse mode, BigDecimal montant,
                         BigDecimal montantRecu, String referenceExterne) {
        this.id = UUID.randomUUID();
        this.mode = mode;
        this.montant = montant;
        this.montantRecu = montantRecu;
        this.referenceExterne = referenceExterne;
    }

    public void finaliser(BigDecimal montantDu) {
        if (mode == null) {
            throw VenteException.invalide("Le mode de paiement est obligatoire");
        }
        if (montant == null || montant.signum() <= 0) {
            throw VenteException.invalide("Le montant du paiement doit etre strictement positif");
        }
        if (montantRecu == null || montantRecu.signum() == 0) {
            this.montantRecu = montant;
        }
        if (mode == ModePaiementCaisse.ESPECES) {
            BigDecimal renduCalcule = montantRecu.subtract(montantDu);
            this.rendu = renduCalcule.signum() > 0 ? renduCalcule.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } else {
            this.rendu = BigDecimal.ZERO;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ModePaiementCaisse getMode() {
        return mode;
    }

    public void setMode(ModePaiementCaisse mode) {
        this.mode = mode;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public BigDecimal getMontantRecu() {
        return montantRecu;
    }

    public void setMontantRecu(BigDecimal montantRecu) {
        this.montantRecu = montantRecu;
    }

    public BigDecimal getRendu() {
        return rendu;
    }

    public void setRendu(BigDecimal rendu) {
        this.rendu = rendu;
    }

    public String getReferenceExterne() {
        return referenceExterne;
    }

    public void setReferenceExterne(String referenceExterne) {
        this.referenceExterne = referenceExterne;
    }
}
