package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "salle", schema = "pedagogie")
public class SalleEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "site_id")
    private UUID siteId;

    @Column(nullable = false)
    private int capacite;

    @JdbcTypeCode(SqlTypes.JSON)
    private String equipements;

    @Column(nullable = false)
    private boolean accessible;

    @Column(nullable = false)
    private boolean disponible;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getSiteId() { return siteId; }
    public void setSiteId(UUID siteId) { this.siteId = siteId; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getEquipements() { return equipements; }
    public void setEquipements(String equipements) { this.equipements = equipements; }
    public boolean isAccessible() { return accessible; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
