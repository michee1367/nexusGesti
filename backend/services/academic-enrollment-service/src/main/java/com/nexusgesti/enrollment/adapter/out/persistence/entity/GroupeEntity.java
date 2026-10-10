package com.nexusgesti.enrollment.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "groupe", schema = "academic_enrollment")
public class GroupeEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String type;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    @Column(name = "salle_id")
    private UUID salleId;

    @Column(name = "creneau_id")
    private UUID creneauId;

    private String jour;

    @Column(nullable = false)
    private int capacite;

    @Column(nullable = false)
    private int effectif;

    @Column(nullable = false)
    private String statut;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public UUID getCreneauId() { return creneauId; }
    public void setCreneauId(UUID creneauId) { this.creneauId = creneauId; }
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public int getEffectif() { return effectif; }
    public void setEffectif(int effectif) { this.effectif = effectif; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
