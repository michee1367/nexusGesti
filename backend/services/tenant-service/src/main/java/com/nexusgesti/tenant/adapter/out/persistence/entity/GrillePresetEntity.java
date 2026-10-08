package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "grille_preset", schema = "tenant")
public class GrillePresetEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 20)
    private String profil;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean systeme;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSysteme() { return systeme; }
    public void setSysteme(boolean systeme) { this.systeme = systeme; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}