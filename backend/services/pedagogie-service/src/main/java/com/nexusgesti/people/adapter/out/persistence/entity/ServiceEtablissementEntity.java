package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "service_etablissement", schema = "pedagogie")
public class ServiceEtablissementEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "responsable_id")
    private UUID responsableId;

    @Column(length = 160)
    private String email;

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
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public UUID getResponsableId() { return responsableId; }
    public void setResponsableId(UUID responsableId) { this.responsableId = responsableId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
