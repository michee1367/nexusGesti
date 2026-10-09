package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "conflit_emploi", schema = "academic_structure")
public class ConflitEmploiEntity {

    @Id
    private UUID id;

    @Column(name = "emploi_id", nullable = false)
    private UUID emploiId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "plage_a_id")
    private UUID plageAId;

    @Column(name = "plage_b_id")
    private UUID plageBId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEmploiId() { return emploiId; }
    public void setEmploiId(UUID emploiId) { this.emploiId = emploiId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getPlageAId() { return plageAId; }
    public void setPlageAId(UUID plageAId) { this.plageAId = plageAId; }
    public UUID getPlageBId() { return plageBId; }
    public void setPlageBId(UUID plageBId) { this.plageBId = plageBId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
