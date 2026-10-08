package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "site", schema = "tenant")
public class SiteEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private boolean principal;

    @Column(name = "adr_rue", length = 120)
    private String adrRue;

    @Column(name = "adr_avenue", length = 120)
    private String adrAvenue;

    @Column(name = "adr_quartier", length = 120)
    private String adrQuartier;

    @Column(name = "adr_commune", length = 120)
    private String adrCommune;

    @Column(name = "adr_ville", length = 120)
    private String adrVille;

    @Column(name = "adr_province", length = 120)
    private String adrProvince;

    @Column(name = "adr_territoire", length = 120)
    private String adrTerritoire;

    @Column(name = "adr_code_postal", length = 20)
    private String adrCodePostal;

    @Column(name = "adr_pays", length = 60)
    private String adrPays;

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
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
    public String getAdrRue() { return adrRue; }
    public void setAdrRue(String adrRue) { this.adrRue = adrRue; }
    public String getAdrAvenue() { return adrAvenue; }
    public void setAdrAvenue(String adrAvenue) { this.adrAvenue = adrAvenue; }
    public String getAdrQuartier() { return adrQuartier; }
    public void setAdrQuartier(String adrQuartier) { this.adrQuartier = adrQuartier; }
    public String getAdrCommune() { return adrCommune; }
    public void setAdrCommune(String adrCommune) { this.adrCommune = adrCommune; }
    public String getAdrVille() { return adrVille; }
    public void setAdrVille(String adrVille) { this.adrVille = adrVille; }
    public String getAdrProvince() { return adrProvince; }
    public void setAdrProvince(String adrProvince) { this.adrProvince = adrProvince; }
    public String getAdrTerritoire() { return adrTerritoire; }
    public void setAdrTerritoire(String adrTerritoire) { this.adrTerritoire = adrTerritoire; }
    public String getAdrCodePostal() { return adrCodePostal; }
    public void setAdrCodePostal(String adrCodePostal) { this.adrCodePostal = adrCodePostal; }
    public String getAdrPays() { return adrPays; }
    public void setAdrPays(String adrPays) { this.adrPays = adrPays; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}