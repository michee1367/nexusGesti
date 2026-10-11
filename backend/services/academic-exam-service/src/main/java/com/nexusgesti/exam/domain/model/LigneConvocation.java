package com.nexusgesti.exam.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class LigneConvocation {

    private UUID id;
    private UUID convocationId;
    private UUID epreuveId;
    private LocalDate date;
    private LocalTime heureDebut;
    private String salle;
    private String numeroPlace;

    public String getLibelle() {
        String heure = heureDebut == null ? "" : heureDebut.toString();
        String lieu = salle == null ? "" : " - " + salle;
        return date + " " + heure + lieu;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConvocationId() { return convocationId; }
    public void setConvocationId(UUID convocationId) { this.convocationId = convocationId; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
    public String getNumeroPlace() { return numeroPlace; }
    public void setNumeroPlace(String numeroPlace) { this.numeroPlace = numeroPlace; }
}
