package com.nexusgesti.exam.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Creneau {

    private UUID id;
    private UUID sessionId;
    private LocalDate date;
    private TypeRetard type;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Integer ordre;

    public Integer getDureeMinutes() {
        if (heureDebut == null || heureFin == null) {
            return null;
        }
        return (int) Duration.between(heureDebut, heureFin).toMinutes();
    }

    public String getLibelle() {
        String base = type == null ? "" : type.name();
        if (heureDebut == null || heureFin == null) {
            return base;
        }
        return base + " " + heureDebut + "-" + heureFin;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TypeRetard getType() { return type; }
    public void setType(TypeRetard type) { this.type = type; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}
