package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "ligne_convocation", schema = "academic_exam")
public class LigneConvocationEntity {

    @Id
    private UUID id;

    @Column(name = "convocation_id", nullable = false)
    private UUID convocationId;

    @Column(name = "epreuve_id")
    private UUID epreuveId;

    @Column(name = "date_ligne")
    private LocalDate dateLigne;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    private String salle;

    @Column(name = "numero_place")
    private String numeroPlace;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConvocationId() { return convocationId; }
    public void setConvocationId(UUID convocationId) { this.convocationId = convocationId; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public LocalDate getDateLigne() { return dateLigne; }
    public void setDateLigne(LocalDate dateLigne) { this.dateLigne = dateLigne; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
    public String getNumeroPlace() { return numeroPlace; }
    public void setNumeroPlace(String numeroPlace) { this.numeroPlace = numeroPlace; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}