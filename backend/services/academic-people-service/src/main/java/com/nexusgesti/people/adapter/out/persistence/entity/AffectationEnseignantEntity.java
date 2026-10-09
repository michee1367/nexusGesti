package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "affectation_enseignant", schema = "academic_people")
public class AffectationEnseignantEntity {

    @Id
    private UUID id;

    @Column(name = "enseignant_id", nullable = false)
    private UUID enseignantId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "matiere_id")
    private UUID matiereId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false)
    private boolean titulaire;

    @Column(name = "volume_horaire_hebdo")
    private BigDecimal volumeHoraireHebdo;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    private boolean principale;

    @Column(name = "remplacement_de")
    private UUID remplacementDe;

    @Column(length = 255)
    private String motif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
