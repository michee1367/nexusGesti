package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "decision_ue", schema = "pedagogie")
public class DecisionUeEntity {

    @Id
    private UUID id;

    @Column(name = "deliberation_id", nullable = false)
    private UUID deliberationId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(name = "moyenne_ue")
    private BigDecimal moyenneUe;

    private BigDecimal credits;

    @Column(nullable = false)
    private String decision;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeliberationId() { return deliberationId; }
    public void setDeliberationId(UUID deliberationId) { this.deliberationId = deliberationId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public BigDecimal getMoyenneUe() { return moyenneUe; }
    public void setMoyenneUe(BigDecimal moyenneUe) { this.moyenneUe = moyenneUe; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}