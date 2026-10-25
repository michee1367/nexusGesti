package com.nexusgesti.attendance.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "seance", schema = "pedagogie")
public class SeanceEntity {

    @Id
    private UUID id;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    @Column(name = "salle_id")
    private UUID salleId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "periode_id")
    private UUID periodeId;

    private LocalDate date;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "heure_fin")
    private LocalTime heureFin;

    @Column(name = "volume_horaire_reel")
    private BigDecimal volumeHoraireReel;

    private String type;

    private String statut;

    @Column(nullable = false)
    private boolean annulee;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public BigDecimal getVolumeHoraireReel() { return volumeHoraireReel; }
    public void setVolumeHoraireReel(BigDecimal volumeHoraireReel) { this.volumeHoraireReel = volumeHoraireReel; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isAnnulee() { return annulee; }
    public void setAnnulee(boolean annulee) { this.annulee = annulee; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
