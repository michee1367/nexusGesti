package com.nexusgesti.identity.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "permission", schema = "socle")
public class PermissionEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(nullable = false, length = 120)
    private String code;

    @Column(length = 150)
    private String libelle;

    @Column(length = 60)
    private String service;

    @Column(length = 120)
    private String ressource;

    @Column(length = 40)
    private String action;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getRessource() { return ressource; }
    public void setRessource(String ressource) { this.ressource = ressource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
