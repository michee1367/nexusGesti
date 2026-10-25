package com.nexusgesti.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public class PersonneLien {
    private UUID id;
    private TypePersonne type;
    private String reference;
    private String matricule;
    private UUID etablissement;
    private Instant dateDebut;
    private Instant dateFin;
    private boolean actif;

    public boolean estActive(Instant date) {
        if (!actif || dateDebut == null) {
            return false;
        }
        return !date.isBefore(dateDebut) && (dateFin == null || !date.isAfter(dateFin));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public TypePersonne getType() { return type; }
    public void setType(TypePersonne type) { this.type = type; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public UUID getEtablissement() { return etablissement; }
    public void setEtablissement(UUID etablissement) { this.etablissement = etablissement; }
    public Instant getDateDebut() { return dateDebut; }
    public void setDateDebut(Instant dateDebut) { this.dateDebut = dateDebut; }
    public Instant getDateFin() { return dateFin; }
    public void setDateFin(Instant dateFin) { this.dateFin = dateFin; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
