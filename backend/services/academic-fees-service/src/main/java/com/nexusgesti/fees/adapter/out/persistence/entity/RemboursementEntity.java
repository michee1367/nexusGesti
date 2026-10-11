package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "remboursement", schema = "academic_fees")
public class RemboursementEntity {

    @Id
    private UUID id;

    @Column(name = "paiement_id", nullable = false)
    private UUID paiementId;

    @Column(nullable = false)
    private BigDecimal montant;

    private String motif;

    private LocalDate date;

    private String moyen;

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getMoyen() { return moyen; }
    public void setMoyen(String moyen) { this.moyen = moyen; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}