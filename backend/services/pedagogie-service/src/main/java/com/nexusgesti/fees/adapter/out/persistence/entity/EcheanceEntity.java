package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "echeance", schema = "pedagogie")
public class EcheanceEntity {

    @Id
    private UUID id;

    @Column(name = "ligne_id", nullable = false)
    private UUID ligneId;

    @Column(nullable = false)
    private Integer numero;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "montant_regle", nullable = false)
    private BigDecimal montantRegle;

    @Column(nullable = false)
    private String statut;

    @Column(name = "date_reglement")
    private LocalDate dateReglement;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateReglement() { return dateReglement; }
    public void setDateReglement(LocalDate dateReglement) { this.dateReglement = dateReglement; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}