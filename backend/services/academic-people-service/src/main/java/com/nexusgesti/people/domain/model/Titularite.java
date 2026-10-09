package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Titularite {

    private UUID id;
    private UUID classeId;
    private UUID enseignantId;
    private LocalDate dateDelegation;
    private LocalDate dateRetrait;
    private String type;
    private String motif;

    public boolean estActive(LocalDate date) {
        boolean apres = dateDelegation == null || !date.isBefore(dateDelegation);
        boolean avant = dateRetrait == null || !date.isAfter(dateRetrait);
        return apres && avant;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public LocalDate getDateDelegation() { return dateDelegation; }
    public void setDateDelegation(LocalDate dateDelegation) { this.dateDelegation = dateDelegation; }
    public LocalDate getDateRetrait() { return dateRetrait; }
    public void setDateRetrait(LocalDate dateRetrait) { this.dateRetrait = dateRetrait; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
