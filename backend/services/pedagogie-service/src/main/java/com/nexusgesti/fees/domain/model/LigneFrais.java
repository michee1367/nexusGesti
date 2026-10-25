package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LigneFrais {

    private UUID id;
    private UUID avisId;
    private TypeFrais typeFrais;
    private String libelle;
    private BigDecimal montantBrut = BigDecimal.ZERO;
    private BigDecimal tauxReduction;
    private BigDecimal montantReduction = BigDecimal.ZERO;
    private BigDecimal montantNet = BigDecimal.ZERO;
    private Integer nbEcheances = 1;
    private String anneeReference;
    private List<Echeance> echeances = new ArrayList<>();

    public BigDecimal getMontantDu() {
        BigDecimal regle = echeances.stream()
                .map(Echeance::getMontantRegle)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return montantNet.subtract(regle);
    }

    public void calculerNet() {
        this.montantNet = montantBrut.subtract(montantReduction);
    }

    public void genererEcheances() {
        this.echeances.clear();
        int nb = nbEcheances == null || nbEcheances < 1 ? 1 : nbEcheances;
        BigDecimal part = montantNet.divide(BigDecimal.valueOf(nb), 2, RoundingMode.HALF_UP);
        BigDecimal cumul = BigDecimal.ZERO;
        for (int i = 1; i <= nb; i++) {
            Echeance echeance = new Echeance();
            echeance.setId(UUID.randomUUID());
            echeance.setLigneId(id);
            echeance.setNumero(i);
            if (i == nb) {
                echeance.setMontant(montantNet.subtract(cumul));
            } else {
                echeance.setMontant(part);
                cumul = cumul.add(part);
            }
            echeance.setMontantRegle(BigDecimal.ZERO);
            echeance.setStatut(StatutEcheance.A_VENIR);
            this.echeances.add(echeance);
        }
    }

    public void ajouterEcheance(Echeance echeance) {
        if (echeance.getId() == null) {
            echeance.setId(UUID.randomUUID());
        }
        echeance.setLigneId(id);
        this.echeances.add(echeance);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvisId() { return avisId; }
    public void setAvisId(UUID avisId) { this.avisId = avisId; }
    public TypeFrais getTypeFrais() { return typeFrais; }
    public void setTypeFrais(TypeFrais typeFrais) { this.typeFrais = typeFrais; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontantBrut() { return montantBrut; }
    public void setMontantBrut(BigDecimal montantBrut) { this.montantBrut = montantBrut; }
    public BigDecimal getTauxReduction() { return tauxReduction; }
    public void setTauxReduction(BigDecimal tauxReduction) { this.tauxReduction = tauxReduction; }
    public BigDecimal getMontantReduction() { return montantReduction; }
    public void setMontantReduction(BigDecimal montantReduction) { this.montantReduction = montantReduction; }
    public BigDecimal getMontantNet() { return montantNet; }
    public void setMontantNet(BigDecimal montantNet) { this.montantNet = montantNet; }
    public Integer getNbEcheances() { return nbEcheances; }
    public void setNbEcheances(Integer nbEcheances) { this.nbEcheances = nbEcheances; }
    public String getAnneeReference() { return anneeReference; }
    public void setAnneeReference(String anneeReference) { this.anneeReference = anneeReference; }
    public List<Echeance> getEcheances() { return echeances; }
    public void setEcheances(List<Echeance> echeances) { this.echeances = echeances == null ? new ArrayList<>() : new ArrayList<>(echeances); }
}
