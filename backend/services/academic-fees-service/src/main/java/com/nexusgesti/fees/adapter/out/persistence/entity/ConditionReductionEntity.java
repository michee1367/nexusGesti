package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "condition_reduction", schema = "academic_fees")
public class ConditionReductionEntity {

    @Id
    private UUID id;

    @Column(name = "reduction_id", nullable = false)
    private UUID reductionId;

    @Column(nullable = false)
    private String critere;

    @Column(nullable = false)
    private String operateur;

    @Column(nullable = false)
    private String valeur;

    @Column(nullable = false)
    private boolean cumulable;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReductionId() { return reductionId; }
    public void setReductionId(UUID reductionId) { this.reductionId = reductionId; }
    public String getCritere() { return critere; }
    public void setCritere(String critere) { this.critere = critere; }
    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public boolean isCumulable() { return cumulable; }
    public void setCumulable(boolean cumulable) { this.cumulable = cumulable; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}