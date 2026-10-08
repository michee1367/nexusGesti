package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grille_notation", schema = "tenant")
public class GrilleNotationEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false, length = 20)
    private String profil;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "date_activation")
    private Instant dateActivation;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean modifiable;

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
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}