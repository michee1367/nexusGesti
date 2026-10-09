package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "titularite", schema = "academic_people")
public class TitulariteEntity {

    @Id
    private UUID id;

    @Column(name = "classe_id", nullable = false)
    private UUID classeId;

    @Column(name = "enseignant_id", nullable = false)
    private UUID enseignantId;

    @Column(name = "date_delegation", nullable = false)
    private LocalDate dateDelegation;

    @Column(name = "date_retrait")
    private LocalDate dateRetrait;

    @Column(length = 30)
    private String type;

    @Column(length = 255)
    private String motif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public LocalDate getDateDelegation() { return dateDelegation; }
    public void setDateDelegation(LocalDate dateDelegation) { this.dateDelegation = dateDelegation; }
    public LocalDate getDateRetrait() { return dateRetrait; }
    public void setDateRetrait(LocalDate dateRetrait) { this.dateRetrait = dateRetrait; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
