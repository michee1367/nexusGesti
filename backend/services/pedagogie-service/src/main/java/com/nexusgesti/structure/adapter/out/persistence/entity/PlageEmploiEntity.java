package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "plage_emploi", schema = "pedagogie")
public class PlageEmploiEntity {

    @Id
    private UUID id;

    @Column(name = "emploi_id", nullable = false)
    private UUID emploiId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "creneau_id")
    private UUID creneauId;

    @Column(name = "salle_id")
    private UUID salleId;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEmploiId() { return emploiId; }
    public void setEmploiId(UUID emploiId) { this.emploiId = emploiId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getCreneauId() { return creneauId; }
    public void setCreneauId(UUID creneauId) { this.creneauId = creneauId; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
