package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_frais", schema = "academic_fees")
public class LigneFraisEntity {

    @Id
    private UUID id;

    @Column(name = "avis_id", nullable = false)
    private UUID avisId;

    @Column(name = "type_frais", nullable = false)
    private String typeFrais;

    private String libelle;

    @Column(name = "montant_brut", nullable = false)
    private BigDecimal montantBrut;

    @Column(name = "taux_reduction")
    private BigDecimal tauxReduction;

    @Column(name = "montant_reduction", nullable = false)
    private BigDecimal montantReduction;

    @Column(name = "montant_net", nullable = false)
    private BigDecimal montantNet;

    @Column(name = "nb_echeances", nullable = false)
    private Integer nbEcheances;

    @Column(name = "annee_reference")
    private String anneeReference;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvisId() { return avisId; }
    public void setAvisId(UUID avisId) { this.avisId = avisId; }
    public String getTypeFrais() { return typeFrais; }
    public void setTypeFrais(String typeFrais) { this.typeFrais = typeFrais; }
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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}