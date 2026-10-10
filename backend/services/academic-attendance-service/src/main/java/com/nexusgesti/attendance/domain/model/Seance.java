package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Seance {

    private UUID idSeance;
    private UUID classeId;
    private UUID coursId;
    private UUID enseignantId;
    private UUID salleId;
    private UUID anneeId;
    private UUID periodeId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private BigDecimal volumeHoraireReel;
    private TypeSeance type;
    private StatutSeance statut;
    private boolean annulee;
    private String motifAnnulation;

    public Seance demarrer() {
        if (statut != null && statut != StatutSeance.PLANIFIEE && statut != StatutSeance.REPORTEE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutSeance.EN_COURS.name());
        }
        this.statut = StatutSeance.EN_COURS;
        return this;
    }

    public Seance cloturer() {
        if (statut != StatutSeance.EN_COURS) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutSeance.TERMINEE.name());
        }
        this.statut = StatutSeance.TERMINEE;
        return this;
    }

    public Seance reporter() {
        if (statut == StatutSeance.TERMINEE || statut == StatutSeance.ANNULEE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutSeance.REPORTEE.name());
        }
        this.statut = StatutSeance.REPORTEE;
        return this;
    }

    public Seance annuler(String motif) {
        if (statut == StatutSeance.TERMINEE) {
            throw AttendanceException.transitionInvalide(StatutSeance.TERMINEE.name(), StatutSeance.ANNULEE.name());
        }
        this.statut = StatutSeance.ANNULEE;
        this.annulee = true;
        this.motifAnnulation = motif;
        return this;
    }

    public Duration getDureeReelle() {
        if (heureDebut == null || heureFin == null) {
            return Duration.ZERO;
        }
        return Duration.between(heureDebut, heureFin);
    }

    public UUID getIdSeance() { return idSeance; }
    public void setIdSeance(UUID idSeance) { this.idSeance = idSeance; }
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
    public TypeSeance getType() { return type; }
    public void setType(TypeSeance type) { this.type = type; }
    public StatutSeance getStatut() { return statut; }
    public void setStatut(StatutSeance statut) { this.statut = statut; }
    public boolean isAnnulee() { return annulee; }
    public void setAnnulee(boolean annulee) { this.annulee = annulee; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
}
