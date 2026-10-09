package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "emploi_du_temps", schema = "academic_structure")
public class EmploiDuTempsEntity {

    @Id
    private UUID id;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(nullable = false)
    private int version;

    @Column(name = "date_activation")
    private Instant dateActivation;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean publie;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
