package com.nexusgesti.attendance.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "presence_eleve", schema = "pedagogie")
public class PresenceEleveEntity {

    @Id
    private UUID id;

    @Column(name = "feuille_id", nullable = false)
    private UUID feuilleId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(nullable = false)
    private String statut;

    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;

    @Column(name = "minutes_retard")
    private Integer minutesRetard;

    @Column(name = "heure_depart")
    private LocalTime heureDepart;

    @Column(name = "depart_anticipe", nullable = false)
    private boolean departAnticipe;

    @Column(name = "type_absence")
    private String typeAbsence;

    @Column(name = "justificatif_id")
    private UUID justificatifId;

    @Column(name = "saisie_par")
    private UUID saisiePar;

    @Column(name = "saisie_le")
    private Instant saisieLe;

    @Column(nullable = false)
    private boolean justifiee;

    @Column(name = "justifiee_le")
    private Instant justifieeLe;

    @Column(name = "modifiee_apres_validation", nullable = false)
    private boolean modifieeApresValidation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFeuilleId() { return feuilleId; }
    public void setFeuilleId(UUID feuilleId) { this.feuilleId = feuilleId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalTime heureArrivee) { this.heureArrivee = heureArrivee; }
    public Integer getMinutesRetard() { return minutesRetard; }
    public void setMinutesRetard(Integer minutesRetard) { this.minutesRetard = minutesRetard; }
    public LocalTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalTime heureDepart) { this.heureDepart = heureDepart; }
    public boolean isDepartAnticipe() { return departAnticipe; }
    public void setDepartAnticipe(boolean departAnticipe) { this.departAnticipe = departAnticipe; }
    public String getTypeAbsence() { return typeAbsence; }
    public void setTypeAbsence(String typeAbsence) { this.typeAbsence = typeAbsence; }
    public UUID getJustificatifId() { return justificatifId; }
    public void setJustificatifId(UUID justificatifId) { this.justificatifId = justificatifId; }
    public UUID getSaisiePar() { return saisiePar; }
    public void setSaisiePar(UUID saisiePar) { this.saisiePar = saisiePar; }
    public Instant getSaisieLe() { return saisieLe; }
    public void setSaisieLe(Instant saisieLe) { this.saisieLe = saisieLe; }
    public boolean isJustifiee() { return justifiee; }
    public void setJustifiee(boolean justifiee) { this.justifiee = justifiee; }
    public Instant getJustifieeLe() { return justifieeLe; }
    public void setJustifieeLe(Instant justifieeLe) { this.justifieeLe = justifieeLe; }
    public boolean isModifieeApresValidation() { return modifieeApresValidation; }
    public void setModifieeApresValidation(boolean modifieeApresValidation) { this.modifieeApresValidation = modifieeApresValidation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
