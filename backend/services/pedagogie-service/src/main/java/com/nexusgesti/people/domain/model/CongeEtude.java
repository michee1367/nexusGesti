package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class CongeEtude {

    private UUID id;
    private UUID eleveId;
    private TypeConge type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private UUID justificatifId;
    private LocalDate reprisePrevue;

    public boolean estEnCours(LocalDate date) {
        return !date.isBefore(dateDebut) && !date.isAfter(dateFin);
    }

    public boolean estEcheance(LocalDate date) {
        return dateFin != null && dateFin.isEqual(date);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public TypeConge getType() { return type; }
    public void setType(TypeConge type) { this.type = type; }
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
}
