package com.nexusgesti.enrollment.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "membre_groupe", schema = "academic_enrollment")
public class MembreGroupeEntity {

    @Id
    private UUID id;

    @Column(name = "groupe_id", nullable = false)
    private UUID groupeId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "date_affectation")
    private LocalDate dateAffectation;

    @Column(name = "affecte_par")
    private UUID affectePar;

    private Integer rang;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGroupeId() { return groupeId; }
    public void setGroupeId(UUID groupeId) { this.groupeId = groupeId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public LocalDate getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(LocalDate dateAffectation) { this.dateAffectation = dateAffectation; }
    public UUID getAffectePar() { return affectePar; }
    public void setAffectePar(UUID affectePar) { this.affectePar = affectePar; }
    public Integer getRang() { return rang; }
    public void setRang(Integer rang) { this.rang = rang; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
