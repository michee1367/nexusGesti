package com.nexusgesti.people.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class AffectationEnseignant {

    private UUID id;
    private UUID enseignantId;
    private UUID classeId;
    private UUID matiereId;
    private TypeAffection type;
    private boolean titulaire;
    private BigDecimal volumeHoraireHebdo;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean principale;
    private UUID remplacementDe;
    private String motif;

    public AffectationEnseignant rendreTitulaire() {
        this.titulaire = true;
        return this;
    }

    public AffectationEnseignant cederTitularite() {
        this.titulaire = false;
        return this;
    }

    public boolean estActive(LocalDate date) {
        boolean apres = dateDebut == null || !date.isBefore(dateDebut);
        boolean avant = dateFin == null || !date.isAfter(dateFin);
        return apres && avant;
    }

    public BigDecimal getChargeHoraireHebdo() {
        return volumeHoraireHebdo;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public TypeAffection getType() { return type; }
    public void setType(TypeAffection type) { this.type = type; }
    public boolean isTitulaire() { return titulaire; }
    public void setTitulaire(boolean titulaire) { this.titulaire = titulaire; }
    public BigDecimal getVolumeHoraireHebdo() { return volumeHoraireHebdo; }
    public void setVolumeHoraireHebdo(BigDecimal volumeHoraireHebdo) { this.volumeHoraireHebdo = volumeHoraireHebdo; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public boolean isPrincipale() { return principale; }
    public void setPrincipale(boolean principale) { this.principale = principale; }
    public UUID getRemplacementDe() { return remplacementDe; }
    public void setRemplacementDe(UUID remplacementDe) { this.remplacementDe = remplacementDe; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
