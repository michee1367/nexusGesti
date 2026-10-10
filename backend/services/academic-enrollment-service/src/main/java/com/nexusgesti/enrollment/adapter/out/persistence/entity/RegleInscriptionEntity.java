package com.nexusgesti.enrollment.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regle_inscription", schema = "academic_enrollment")
public class RegleInscriptionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "programme_id")
    private UUID programmeId;

    @Column(name = "condition_regle")
    private String condition;

    @Column(name = "credits_min")
    private BigDecimal creditsMin;

    @Column(name = "moyenne_min")
    private BigDecimal moyenneMin;

    @Column(name = "creneau_age")
    private Integer creneauAge;

    @Column(nullable = false)
    private int priorite;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public BigDecimal getCreditsMin() { return creditsMin; }
    public void setCreditsMin(BigDecimal creditsMin) { this.creditsMin = creditsMin; }
    public BigDecimal getMoyenneMin() { return moyenneMin; }
    public void setMoyenneMin(BigDecimal moyenneMin) { this.moyenneMin = moyenneMin; }
    public Integer getCreneauAge() { return creneauAge; }
    public void setCreneauAge(Integer creneauAge) { this.creneauAge = creneauAge; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
