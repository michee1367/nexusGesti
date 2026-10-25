package com.nexusgesti.boulangerie.production.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "plan_production", schema = "boulangerie")
public class PlanProductionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "atelier_code")
    private String atelierCode;

    @Column(nullable = false)
    private String statut;

    @Column(name = "besoins_matiere")
    @JdbcTypeCode(SqlTypes.JSON)
    private String besoinsMatiere;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getAtelierCode() { return atelierCode; }
    public void setAtelierCode(String atelierCode) { this.atelierCode = atelierCode; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getBesoinsMatiere() { return besoinsMatiere; }
    public void setBesoinsMatiere(String besoinsMatiere) { this.besoinsMatiere = besoinsMatiere; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
