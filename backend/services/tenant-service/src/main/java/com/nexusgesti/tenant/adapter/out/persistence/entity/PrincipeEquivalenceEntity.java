package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "principe_equivalence", schema = "tenant")
public class PrincipeEquivalenceEntity {

    @Id
    private UUID id;

    @Column(name = "regle_equivalence_id", nullable = false)
    private UUID regleEquivalenceId;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(length = 60)
    private String cible;

    @Column
    private BigDecimal ponderation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRegleEquivalenceId() { return regleEquivalenceId; }
    public void setRegleEquivalenceId(UUID regleEquivalenceId) { this.regleEquivalenceId = regleEquivalenceId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
    public BigDecimal getPonderation() { return ponderation; }
    public void setPonderation(BigDecimal ponderation) { this.ponderation = ponderation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}