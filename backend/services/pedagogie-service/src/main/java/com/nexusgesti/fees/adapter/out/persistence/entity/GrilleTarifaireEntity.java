package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "grille_tarifaire", schema = "pedagogie")
public class GrilleTarifaireEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    private UUID annee;

    private String profil;

    @Column(nullable = false)
    private String statut;

    private String devise;

    @Column(name = "date_activation")
    private LocalDate dateActivation;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private boolean modifiable;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getAnnee() { return annee; }
    public void setAnnee(UUID annee) { this.annee = annee; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public LocalDate getDateActivation() { return dateActivation; }
    public void setDateActivation(LocalDate dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}