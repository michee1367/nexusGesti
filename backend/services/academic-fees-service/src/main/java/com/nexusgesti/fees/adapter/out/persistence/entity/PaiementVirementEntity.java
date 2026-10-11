package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "paiement_virement", schema = "academic_fees")
public class PaiementVirementEntity {

    @Id
    private UUID id;

    @Column(name = "paiement_id", nullable = false)
    private UUID paiementId;

    private String banque;

    @Column(name = "numero_compte")
    private String numeroCompte;

    @Column(name = "reference_virement")
    private String referenceVirement;

    @Column(name = "date_valeur")
    private LocalDate dateValeur;

    @Column(nullable = false)
    private BigDecimal commission;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public String getBanque() { return banque; }
    public void setBanque(String banque) { this.banque = banque; }
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    public String getReferenceVirement() { return referenceVirement; }
    public void setReferenceVirement(String referenceVirement) { this.referenceVirement = referenceVirement; }
    public LocalDate getDateValeur() { return dateValeur; }
    public void setDateValeur(LocalDate dateValeur) { this.dateValeur = dateValeur; }
    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}