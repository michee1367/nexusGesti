package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "alerte_stock", schema = "socle")
public class AlerteStockEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 60)
    private String reference;

    @Column(length = 60)
    private String depot;

    @Column(nullable = false)
    private BigDecimal seuil;

    @Column(nullable = false, length = 20)
    private String niveau;

    @JdbcTypeCode(SqlTypes.JSON)
    private String destinataires;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDepot() { return depot; }
    public void setDepot(String depot) { this.depot = depot; }
    public BigDecimal getSeuil() { return seuil; }
    public void setSeuil(BigDecimal seuil) { this.seuil = seuil; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getDestinataires() { return destinataires; }
    public void setDestinataires(String destinataires) { this.destinataires = destinataires; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}