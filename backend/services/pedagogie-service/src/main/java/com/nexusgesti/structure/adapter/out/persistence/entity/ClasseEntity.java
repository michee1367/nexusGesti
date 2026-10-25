package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "classe", schema = "pedagogie")
public class ClasseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "niveau_id")
    private UUID niveauId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "site_id")
    private UUID siteId;

    private Integer capacite;

    @Column(name = "effectif_reel", nullable = false)
    private int effectifReel;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(name = "salle_attitree_id")
    private UUID salleAttitreeId;

    @Column(name = "regime_horaire_code", length = 30)
    private String regimeHoraireCode;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getNiveauId() { return niveauId; }
    public void setNiveauId(UUID niveauId) { this.niveauId = niveauId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getSiteId() { return siteId; }
    public void setSiteId(UUID siteId) { this.siteId = siteId; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public int getEffectifReel() { return effectifReel; }
    public void setEffectifReel(int effectifReel) { this.effectifReel = effectifReel; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getSalleAttitreeId() { return salleAttitreeId; }
    public void setSalleAttitreeId(UUID salleAttitreeId) { this.salleAttitreeId = salleAttitreeId; }
    public String getRegimeHoraireCode() { return regimeHoraireCode; }
    public void setRegimeHoraireCode(String regimeHoraireCode) { this.regimeHoraireCode = regimeHoraireCode; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
