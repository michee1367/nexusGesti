package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regle_equivalence", schema = "socle")
public class RegleEquivalenceEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 30)
    private String strategie;

    @Column(name = "nombre_max_ignorees")
    private Integer nombreMaxIgnorees;

    @Column(name = "moyenne_minimale_ignoree")
    private BigDecimal moyenneMinimaleIgnoree;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getStrategie() { return strategie; }
    public void setStrategie(String strategie) { this.strategie = strategie; }
    public Integer getNombreMaxIgnorees() { return nombreMaxIgnorees; }
    public void setNombreMaxIgnorees(Integer nombreMaxIgnorees) { this.nombreMaxIgnorees = nombreMaxIgnorees; }
    public BigDecimal getMoyenneMinimaleIgnoree() { return moyenneMinimaleIgnoree; }
    public void setMoyenneMinimaleIgnoree(BigDecimal moyenneMinimaleIgnoree) { this.moyenneMinimaleIgnoree = moyenneMinimaleIgnoree; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}