package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "imputation", schema = "pedagogie")
public class ImputationEntity {

    @Id
    private UUID id;

    @Column(name = "paiement_id", nullable = false)
    private UUID paiementId;

    @Column(name = "echeance_id", nullable = false)
    private UUID echeanceId;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "date_imputation")
    private LocalDate dateImputation;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public UUID getEcheanceId() { return echeanceId; }
    public void setEcheanceId(UUID echeanceId) { this.echeanceId = echeanceId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDate getDateImputation() { return dateImputation; }
    public void setDateImputation(LocalDate dateImputation) { this.dateImputation = dateImputation; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}