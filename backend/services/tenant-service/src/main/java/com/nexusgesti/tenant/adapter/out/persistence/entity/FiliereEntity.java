package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "filiere", schema = "tenant")
public class FiliereEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(length = 120)
    private String departement;

    @Column(length = 120)
    private String diplome;

    @Column(name = "niveau_diplome", length = 30)
    private String niveauDiplome;

    @Column(name = "duree_annees")
    private Integer dureeAnnees;

    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(nullable = false)
    private boolean active;

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
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    public String getNiveauDiplome() { return niveauDiplome; }
    public void setNiveauDiplome(String niveauDiplome) { this.niveauDiplome = niveauDiplome; }
    public Integer getDureeAnnees() { return dureeAnnees; }
    public void setDureeAnnees(Integer dureeAnnees) { this.dureeAnnees = dureeAnnees; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}