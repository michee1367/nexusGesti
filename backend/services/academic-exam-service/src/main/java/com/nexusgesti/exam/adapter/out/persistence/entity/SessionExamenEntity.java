package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "session_examen", schema = "academic_exam")
public class SessionExamenEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String type;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "periode_id")
    private UUID periodeId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "date_ouverture")
    private LocalDate dateOuverture;

    @Column(name = "date_cloture")
    private LocalDate dateCloture;

    @Column(nullable = false)
    private String statut;

    @Column(name = "annee_universitaire", nullable = false)
    private boolean anneeUniversitaire;

    @Column(name = "convocation_emise", nullable = false)
    private boolean convocationEmise;

    @Column(name = "proces_verbal_id")
    private UUID procesVerbalId;

    @Column(name = "grille_deliberation")
    private String grilleDeliberation;

    @Column(name = "nb_candidats_attendus")
    private Integer nbCandidatsAttendus;

    @Column(name = "nb_candidats_convoques")
    private Integer nbCandidatsConvoques;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "annule_le")
    private Instant annuleLe;

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
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public LocalDate getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDate dateOuverture) { this.dateOuverture = dateOuverture; }
    public LocalDate getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDate dateCloture) { this.dateCloture = dateCloture; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isAnneeUniversitaire() { return anneeUniversitaire; }
    public void setAnneeUniversitaire(boolean anneeUniversitaire) { this.anneeUniversitaire = anneeUniversitaire; }
    public boolean isConvocationEmise() { return convocationEmise; }
    public void setConvocationEmise(boolean convocationEmise) { this.convocationEmise = convocationEmise; }
    public UUID getProcesVerbalId() { return procesVerbalId; }
    public void setProcesVerbalId(UUID procesVerbalId) { this.procesVerbalId = procesVerbalId; }
    public String getGrilleDeliberation() { return grilleDeliberation; }
    public void setGrilleDeliberation(String grilleDeliberation) { this.grilleDeliberation = grilleDeliberation; }
    public Integer getNbCandidatsAttendus() { return nbCandidatsAttendus; }
    public void setNbCandidatsAttendus(Integer nbCandidatsAttendus) { this.nbCandidatsAttendus = nbCandidatsAttendus; }
    public Integer getNbCandidatsConvoques() { return nbCandidatsConvoques; }
    public void setNbCandidatsConvoques(Integer nbCandidatsConvoques) { this.nbCandidatsConvoques = nbCandidatsConvoques; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public Instant getAnnuleLe() { return annuleLe; }
    public void setAnnuleLe(Instant annuleLe) { this.annuleLe = annuleLe; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
