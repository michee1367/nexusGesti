package com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "paiement_vente", schema = "boulangerie_vente")
public class PaiementVenteEntity {

    @Id
    private UUID id;

    @Column(name = "vente_id", nullable = false)
    private UUID venteId;

    @Column(nullable = false)
    private String mode;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(nullable = false)
    private String devise;

    @Column(name = "montant_recu", nullable = false)
    private BigDecimal montantRecu;

    @Column(nullable = false)
    private BigDecimal rendu;

    @Column(name = "reference_externe")
    private String referenceExterne;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVenteId() { return venteId; }
    public void setVenteId(UUID venteId) { this.venteId = venteId; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public BigDecimal getMontantRecu() { return montantRecu; }
    public void setMontantRecu(BigDecimal montantRecu) { this.montantRecu = montantRecu; }
    public BigDecimal getRendu() { return rendu; }
    public void setRendu(BigDecimal rendu) { this.rendu = rendu; }
    public String getReferenceExterne() { return referenceExterne; }
    public void setReferenceExterne(String referenceExterne) { this.referenceExterne = referenceExterne; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
