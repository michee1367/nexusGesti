package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sanction_disciplinaire", schema = "academic_people")
public class SanctionDisciplinaireEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "date_sanction", nullable = false)
    private LocalDate dateSanction;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(length = 255)
    private String motif;

    @Column(name = "duree_jours")
    private Integer dureeJours;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "decide_par")
    private UUID decidePar;

    @Column(length = 160)
    private String instance;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public LocalDate getDateSanction() { return dateSanction; }
    public void setDateSanction(LocalDate dateSanction) { this.dateSanction = dateSanction; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public Integer getDureeJours() { return dureeJours; }
    public void setDureeJours(Integer dureeJours) { this.dureeJours = dureeJours; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public UUID getDecidePar() { return decidePar; }
    public void setDecidePar(UUID decidePar) { this.decidePar = decidePar; }
    public String getInstance() { return instance; }
    public void setInstance(String instance) { this.instance = instance; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
