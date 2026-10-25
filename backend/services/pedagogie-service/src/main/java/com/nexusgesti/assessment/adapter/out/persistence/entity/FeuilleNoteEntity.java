package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "feuille_note", schema = "pedagogie")
public class FeuilleNoteEntity {

    @Id
    private UUID id;

    @Column(name = "evaluation_id", nullable = false)
    private UUID evaluationId;

    private String statut;

    @Column(name = "nb_notes_attendues", nullable = false)
    private int nbNotesAttendues;

    @Column(name = "nb_notes_saisies", nullable = false)
    private int nbNotesSaisies;

    @Column(name = "nb_absents", nullable = false)
    private int nbAbsents;

    @Column(name = "ouverte_le")
    private Instant ouverteLe;

    @Column(name = "cloturee_le")
    private Instant clotureeLe;

    @Column(nullable = false)
    private boolean verrouillee;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEvaluationId() { return evaluationId; }
    public void setEvaluationId(UUID evaluationId) { this.evaluationId = evaluationId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getNbNotesAttendues() { return nbNotesAttendues; }
    public void setNbNotesAttendues(int nbNotesAttendues) { this.nbNotesAttendues = nbNotesAttendues; }
    public int getNbNotesSaisies() { return nbNotesSaisies; }
    public void setNbNotesSaisies(int nbNotesSaisies) { this.nbNotesSaisies = nbNotesSaisies; }
    public int getNbAbsents() { return nbAbsents; }
    public void setNbAbsents(int nbAbsents) { this.nbAbsents = nbAbsents; }
    public Instant getOuverteLe() { return ouverteLe; }
    public void setOuverteLe(Instant ouverteLe) { this.ouverteLe = ouverteLe; }
    public Instant getClotureeLe() { return clotureeLe; }
    public void setClotureeLe(Instant clotureeLe) { this.clotureeLe = clotureeLe; }
    public boolean isVerrouillee() { return verrouillee; }
    public void setVerrouillee(boolean verrouillee) { this.verrouillee = verrouillee; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
