package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "evaluation", schema = "academic_assessment")
public class EvaluationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String type;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(name = "semestre_id")
    private UUID semestreId;

    @Column(name = "matiere_id")
    private UUID matiereId;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "periode_id")
    private UUID periodeId;

    @Column(name = "date_evaluation")
    private LocalDate dateEvaluation;

    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(name = "salle_id")
    private UUID salleId;

    @Column(nullable = false)
    private String statut;

    @Column(name = "grille_id")
    private UUID grilleId;

    @Column(name = "grille_version")
    private Integer grilleVersion;

    @Column(name = "bareme_type")
    private String baremeType;

    @Column(name = "note_max")
    private BigDecimal noteMax;

    @Column(name = "coefficient_declare")
    private BigDecimal coefficientDeclare;

    private String consignes;

    @Column(nullable = false)
    private boolean anonymise;

    @Column(name = "comptee_dans_moyenne", nullable = false)
    private boolean compteeDansMoyenne;

    @Column(name = "declenche_session", nullable = false)
    private boolean declencheSession;

    @Column(name = "annule_le")
    private Instant annuleLe;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "cree_le")
    private Instant creeLe;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public Integer getGrilleVersion() { return grilleVersion; }
    public void setGrilleVersion(Integer grilleVersion) { this.grilleVersion = grilleVersion; }
    public String getBaremeType() { return baremeType; }
    public void setBaremeType(String baremeType) { this.baremeType = baremeType; }
    public BigDecimal getNoteMax() { return noteMax; }
    public void setNoteMax(BigDecimal noteMax) { this.noteMax = noteMax; }
    public BigDecimal getCoefficientDeclare() { return coefficientDeclare; }
    public void setCoefficientDeclare(BigDecimal coefficientDeclare) { this.coefficientDeclare = coefficientDeclare; }
    public String getConsignes() { return consignes; }
    public void setConsignes(String consignes) { this.consignes = consignes; }
    public boolean isAnonymise() { return anonymise; }
    public void setAnonymise(boolean anonymise) { this.anonymise = anonymise; }
    public boolean isCompteeDansMoyenne() { return compteeDansMoyenne; }
    public void setCompteeDansMoyenne(boolean compteeDansMoyenne) { this.compteeDansMoyenne = compteeDansMoyenne; }
    public boolean isDeclencheSession() { return declencheSession; }
    public void setDeclencheSession(boolean declencheSession) { this.declencheSession = declencheSession; }
    public Instant getAnnuleLe() { return annuleLe; }
    public void setAnnuleLe(Instant annuleLe) { this.annuleLe = annuleLe; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public Instant getCreeLe() { return creeLe; }
    public void setCreeLe(Instant creeLe) { this.creeLe = creeLe; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
