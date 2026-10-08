package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "annee_scolaire", schema = "tenant")
public class AnneeScolaireEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "date_debut", nullable = false)
    private Instant dateDebut;

    @Column(name = "date_fin", nullable = false)
    private Instant dateFin;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(name = "annee_reference", nullable = false)
    private int anneeReference;

    @Column(name = "site_par_defaut_id")
    private UUID siteParDefautId;

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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Instant getDateDebut() { return dateDebut; }
    public void setDateDebut(Instant dateDebut) { this.dateDebut = dateDebut; }
    public Instant getDateFin() { return dateFin; }
    public void setDateFin(Instant dateFin) { this.dateFin = dateFin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getAnneeReference() { return anneeReference; }
    public void setAnneeReference(int anneeReference) { this.anneeReference = anneeReference; }
    public UUID getSiteParDefautId() { return siteParDefautId; }
    public void setSiteParDefautId(UUID siteParDefautId) { this.siteParDefautId = siteParDefautId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}