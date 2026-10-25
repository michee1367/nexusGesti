package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "paiement_mobile_money", schema = "pedagogie")
public class PaiementMobileMoneyEntity {

    @Id
    private UUID id;

    @Column(name = "paiement_id", nullable = false)
    private UUID paiementId;

    private String operateur;

    @Column(name = "numero_telephone")
    private String numeroTelephone;

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @Column(name = "date_transaction")
    private Instant dateTransaction;

    @Column(name = "statut_transaction")
    private String statutTransaction;

    @Column(nullable = false)
    private BigDecimal frais;

    @Column(name = "montant_recu", nullable = false)
    private BigDecimal montantRecu;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
    public Instant getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(Instant dateTransaction) { this.dateTransaction = dateTransaction; }
    public String getStatutTransaction() { return statutTransaction; }
    public void setStatutTransaction(String statutTransaction) { this.statutTransaction = statutTransaction; }
    public BigDecimal getFrais() { return frais; }
    public void setFrais(BigDecimal frais) { this.frais = frais; }
    public BigDecimal getMontantRecu() { return montantRecu; }
    public void setMontantRecu(BigDecimal montantRecu) { this.montantRecu = montantRecu; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}