package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cycle", schema = "tenant")
public class CycleEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int rang;

    @Column(nullable = false, length = 20)
    private String profil;

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
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}