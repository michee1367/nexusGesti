package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "epreuve", schema = "pedagogie")
public class EpreuveEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "matiere_id")
    private UUID matiereId;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(nullable = false)
    private String type;

    @Column(name = "date_epreuve")
    private LocalDate dateEpreuve;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    private String bareme;

    private BigDecimal coefficient;

    @Column(nullable = false)
    private String statut;

    @Column(name = "comptabilise_dans_moyenne", nullable = false)
    private boolean comptabiliseDansMoyenne;

    @Column(name = "periode_rattachement")
    private UUID periodeRattachement;

    @Column(name = "evaluation_origine")
    private UUID evaluationOrigine;

    @Column(name = "salle_par_defaut")
    private UUID salleParDefaut;

    private String consignes;

    @Column(nullable = false)
    private boolean anonymise;

    @Column(name = "note_eliminatoire")
    private BigDecimal noteEliminatoire;

    @Column(name = "declaree_hors_moyenne", nullable = false)
    private boolean declareeHorsMoyenne;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDateEpreuve() { return dateEpreuve; }
    public void setDateEpreuve(LocalDate dateEpreuve) { this.dateEpreuve = dateEpreuve; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public String getBareme() { return bareme; }
    public void setBareme(String bareme) { this.bareme = bareme; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isComptabiliseDansMoyenne() { return comptabiliseDansMoyenne; }
    public void setComptabiliseDansMoyenne(boolean comptabiliseDansMoyenne) { this.comptabiliseDansMoyenne = comptabiliseDansMoyenne; }
    public UUID getPeriodeRattachement() { return periodeRattachement; }
    public void setPeriodeRattachement(UUID periodeRattachement) { this.periodeRattachement = periodeRattachement; }
    public UUID getEvaluationOrigine() { return evaluationOrigine; }
    public void setEvaluationOrigine(UUID evaluationOrigine) { this.evaluationOrigine = evaluationOrigine; }
    public UUID getSalleParDefaut() { return salleParDefaut; }
    public void setSalleParDefaut(UUID salleParDefaut) { this.salleParDefaut = salleParDefaut; }
    public String getConsignes() { return consignes; }
    public void setConsignes(String consignes) { this.consignes = consignes; }
    public boolean isAnonymise() { return anonymise; }
    public void setAnonymise(boolean anonymise) { this.anonymise = anonymise; }
    public BigDecimal getNoteEliminatoire() { return noteEliminatoire; }
    public void setNoteEliminatoire(BigDecimal noteEliminatoire) { this.noteEliminatoire = noteEliminatoire; }
    public boolean isDeclareeHorsMoyenne() { return declareeHorsMoyenne; }
    public void setDeclareeHorsMoyenne(boolean declareeHorsMoyenne) { this.declareeHorsMoyenne = declareeHorsMoyenne; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
