package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "regime_horaire", schema = "pedagogie")
public class RegimeHoraireEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @JdbcTypeCode(SqlTypes.JSON)
    private String jours;

    @Column(name = "creneaux_par_jour", nullable = false)
    private int creneauxParJour;

    @Column(name = "duree_creneau", nullable = false)
    private int dureeCreneau;

    @Column(name = "pause_matinale")
    private Integer pauseMatinale;

    @Column(name = "pause_meridienne")
    private Integer pauseMeridienne;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getJours() { return jours; }
    public void setJours(String jours) { this.jours = jours; }
    public int getCreneauxParJour() { return creneauxParJour; }
    public void setCreneauxParJour(int creneauxParJour) { this.creneauxParJour = creneauxParJour; }
    public int getDureeCreneau() { return dureeCreneau; }
    public void setDureeCreneau(int dureeCreneau) { this.dureeCreneau = dureeCreneau; }
    public Integer getPauseMatinale() { return pauseMatinale; }
    public void setPauseMatinale(Integer pauseMatinale) { this.pauseMatinale = pauseMatinale; }
    public Integer getPauseMeridienne() { return pauseMeridienne; }
    public void setPauseMeridienne(Integer pauseMeridienne) { this.pauseMeridienne = pauseMeridienne; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
