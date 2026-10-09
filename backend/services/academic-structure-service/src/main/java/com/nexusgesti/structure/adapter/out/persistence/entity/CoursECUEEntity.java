package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cours_ecue", schema = "academic_structure")
public class CoursECUEEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "matiere_id")
    private UUID matiereId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "volume_horaire")
    private BigDecimal volumeHoraire;

    private BigDecimal credits;

    private BigDecimal coefficient;

    @Column(nullable = false)
    private boolean hebdomadaire;

    @Column(length = 60)
    private String regroupement;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(BigDecimal volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public boolean isHebdomadaire() { return hebdomadaire; }
    public void setHebdomadaire(boolean hebdomadaire) { this.hebdomadaire = hebdomadaire; }
    public String getRegroupement() { return regroupement; }
    public void setRegroupement(String regroupement) { this.regroupement = regroupement; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
