package com.nexusgesti.attendance.domain.model;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public class PresenceEleve {

    private UUID idPresence;
    private UUID feuilleId;
    private UUID eleveId;
    private StatutPresence statut;
    private LocalTime heureArrivee;
    private Integer minutesRetard;
    private LocalTime heureDepart;
    private boolean departAnticipe;
    private TypeAbsence typeAbsence;
    private UUID justificatifId;
    private UUID saisiePar;
    private Instant saisieLe;
    private boolean justifiee;
    private Instant justifieeLe;
    private boolean modifieeApresValidation;

    public boolean estPresent() {
        return statut != null && statut.estPresent();
    }

    public boolean estAbsent() {
        return statut != null && statut.estAbsent();
    }

    public boolean estRetard() {
        return statut != null && statut.estRetard();
    }

    public int getMinutesRetard() {
        return minutesRetard == null ? 0 : minutesRetard;
    }

    public PresenceEleve excuser(UUID justificatifId) {
        if (estAbsent()) {
            this.statut = StatutPresence.ABSENT_EXCUSE;
        }
        this.justifiee = true;
        this.justifieeLe = Instant.now();
        this.justificatifId = justificatifId;
        return this;
    }

    public UUID getIdPresence() { return idPresence; }
    public void setIdPresence(UUID idPresence) { this.idPresence = idPresence; }
    public UUID getFeuilleId() { return feuilleId; }
    public void setFeuilleId(UUID feuilleId) { this.feuilleId = feuilleId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public StatutPresence getStatut() { return statut; }
    public void setStatut(StatutPresence statut) { this.statut = statut; }
    public LocalTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalTime heureArrivee) { this.heureArrivee = heureArrivee; }
    public void setMinutesRetard(Integer minutesRetard) { this.minutesRetard = minutesRetard; }
    public LocalTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalTime heureDepart) { this.heureDepart = heureDepart; }
    public boolean isDepartAnticipe() { return departAnticipe; }
    public void setDepartAnticipe(boolean departAnticipe) { this.departAnticipe = departAnticipe; }
    public TypeAbsence getTypeAbsence() { return typeAbsence; }
    public void setTypeAbsence(TypeAbsence typeAbsence) { this.typeAbsence = typeAbsence; }
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
}
