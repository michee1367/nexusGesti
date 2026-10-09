package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "conge_etude", schema = "academic_people")
public class CongeEtudeEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(length = 255)
    private String motif;

    @Column(name = "justificatif_id")
    private UUID justificatifId;

    @Column(name = "reprise_prevue")
    private LocalDate reprisePrevue;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getJustificatifId() { return justificatifId; }
    public void setJustificatifId(UUID justificatifId) { this.justificatifId = justificatifId; }
    public LocalDate getReprisePrevue() { return reprisePrevue; }
    public void setReprisePrevue(LocalDate reprisePrevue) { this.reprisePrevue = reprisePrevue; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
