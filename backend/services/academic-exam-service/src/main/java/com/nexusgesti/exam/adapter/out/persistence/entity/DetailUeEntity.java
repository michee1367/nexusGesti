package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "detail_ue", schema = "academic_exam")
public class DetailUeEntity {

    @Id
    private UUID id;

    @Column(name = "decision_ue_id", nullable = false)
    private UUID decisionUeId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "moyenne_cours")
    private BigDecimal moyenneCours;

    private BigDecimal coefficient;

    @Column(nullable = false)
    private boolean admis;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDecisionUeId() { return decisionUeId; }
    public void setDecisionUeId(UUID decisionUeId) { this.decisionUeId = decisionUeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public BigDecimal getMoyenneCours() { return moyenneCours; }
    public void setMoyenneCours(BigDecimal moyenneCours) { this.moyenneCours = moyenneCours; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public boolean isAdmis() { return admis; }
    public void setAdmis(boolean admis) { this.admis = admis; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}