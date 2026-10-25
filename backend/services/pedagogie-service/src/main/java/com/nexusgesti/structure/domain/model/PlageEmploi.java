package com.nexusgesti.structure.domain.model;

import java.util.UUID;

public class PlageEmploi {

    private UUID idPlage;
    private UUID emploiId;
    private UUID coursId;
    private UUID creneauId;
    private UUID salleId;
    private UUID enseignantId;

    public UUID getIdPlage() { return idPlage; }
    public void setIdPlage(UUID idPlage) { this.idPlage = idPlage; }
    public UUID getEmploiId() { return emploiId; }
    public void setEmploiId(UUID emploiId) { this.emploiId = emploiId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getCreneauId() { return creneauId; }
    public void setCreneauId(UUID creneauId) { this.creneauId = creneauId; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
}
