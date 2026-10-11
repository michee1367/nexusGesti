package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Echeance {

    private UUID id;
    private UUID ligneId;
    private Integer numero;
    private LocalDate dateEcheance;
    private BigDecimal montant = BigDecimal.ZERO;
    private BigDecimal montantRegle = BigDecimal.ZERO;
    private StatutEcheance statut = StatutEcheance.A_VENIR;
    private LocalDate dateReglement;

    public boolean estEchue(LocalDate date) {
        return dateEcheance != null && date != null && date.isAfter(dateEcheance)
                && statut != StatutEcheance.REGLEE && statut != StatutEcheance.ANNULEE;
    }

    public BigDecimal getSolde() {
        return montant.subtract(montantRegle);
    }

    public void enregistrerReglement(BigDecimal valeur, LocalDate date) {
        this.montantRegle = this.montantRegle.add(valeur);
        this.dateReglement = date;
        if (montantRegle.compareTo(montant) >= 0) {
            this.statut = StatutEcheance.REGLEE;
        } else if (montantRegle.signum() > 0) {
            this.statut = StatutEcheance.PARTIELLEMENT_REGLEE;
        }
    }

    public Echeance reporter(LocalDate nouvelleDate) {
        if (statut == StatutEcheance.REGLEE || statut == StatutEcheance.ANNULEE) {
            throw com.nexusgesti.fees.domain.FeesException.etatInvalide("Echeance non reportable : " + statut);
        }
        this.dateEcheance = nouvelleDate;
        this.statut = StatutEcheance.REPORTEE;
        return this;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLigneId() { return ligneId; }
    public void setLigneId(UUID ligneId) { this.ligneId = ligneId; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public BigDecimal getMontantRegle() { return montantRegle; }
    public void setMontantRegle(BigDecimal montantRegle) { this.montantRegle = montantRegle; }
    public StatutEcheance getStatut() { return statut; }
    public void setStatut(StatutEcheance statut) { this.statut = statut; }
    public LocalDate getDateReglement() { return dateReglement; }
    public void setDateReglement(LocalDate dateReglement) { this.dateReglement = dateReglement; }
}
