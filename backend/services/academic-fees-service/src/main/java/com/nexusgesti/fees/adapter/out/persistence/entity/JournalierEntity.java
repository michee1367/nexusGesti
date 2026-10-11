package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "journalier", schema = "academic_fees")
public class JournalierEntity {

    @Id
    private UUID id;

    private LocalDate date;

    private UUID site;

    @Column(nullable = false)
    private String statut;

    @Column(nullable = false)
    private BigDecimal fond;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public UUID getSite() { return site; }
    public void setSite(UUID site) { this.site = site; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getFond() { return fond; }
    public void setFond(BigDecimal fond) { this.fond = fond; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}