package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "etablissement", schema = "tenant")
public class EtablissementEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 150)
    private String denomination;

    @Column(length = 20)
    private String sigle;

    @Column(nullable = false, length = 20)
    private String profil;

    @Column(nullable = false, length = 20)
    private String statut;

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

    @Column(length = 30)
    private String telephone;

    @Column(name = "email_officiel", length = 120)
    private String emailOfficiel;

    @Column(length = 10)
    private String devise;

    @Column(length = 60)
    private String pays;

    @Column(name = "fuseau_horaire", length = 40)
    private String fuseauHoraire;

    @Column(name = "capacite_accueil")
    private Integer capaciteAccueil;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDenomination() { return denomination; }
    public void setDenomination(String denomination) { this.denomination = denomination; }
    public String getSigle() { return sigle; }
    public void setSigle(String sigle) { this.sigle = sigle; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
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
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmailOfficiel() { return emailOfficiel; }
    public void setEmailOfficiel(String emailOfficiel) { this.emailOfficiel = emailOfficiel; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getFuseauHoraire() { return fuseauHoraire; }
    public void setFuseauHoraire(String fuseauHoraire) { this.fuseauHoraire = fuseauHoraire; }
    public Integer getCapaciteAccueil() { return capaciteAccueil; }
    public void setCapaciteAccueil(Integer capaciteAccueil) { this.capaciteAccueil = capaciteAccueil; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}