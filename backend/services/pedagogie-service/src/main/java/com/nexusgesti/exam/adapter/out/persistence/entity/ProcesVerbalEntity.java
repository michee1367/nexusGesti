package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "proces_verbal", schema = "pedagogie")
public class ProcesVerbalEntity {

    @Id
    private UUID id;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private String statut;

    @Column(name = "date_redaction")
    private LocalDate dateRedaction;

    @Column(name = "redige_par")
    private UUID redigePar;

    @Column(name = "presence_directeur", nullable = false)
    private boolean presenceDirecteur;

    private String observations;

    @Column(name = "nb_absences", nullable = false)
    private int nbAbsences;

    @Column(name = "nb_incidents", nullable = false)
    private int nbIncidents;

    @Column(name = "date_cloture")
    private Instant dateCloture;

    @Column(name = "motif_contestation")
    private String motifContestation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateRedaction() { return dateRedaction; }
    public void setDateRedaction(LocalDate dateRedaction) { this.dateRedaction = dateRedaction; }
    public UUID getRedigePar() { return redigePar; }
    public void setRedigePar(UUID redigePar) { this.redigePar = redigePar; }
    public boolean isPresenceDirecteur() { return presenceDirecteur; }
    public void setPresenceDirecteur(boolean presenceDirecteur) { this.presenceDirecteur = presenceDirecteur; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public int getNbAbsences() { return nbAbsences; }
    public void setNbAbsences(int nbAbsences) { this.nbAbsences = nbAbsences; }
    public int getNbIncidents() { return nbIncidents; }
    public void setNbIncidents(int nbIncidents) { this.nbIncidents = nbIncidents; }
    public Instant getDateCloture() { return dateCloture; }
    public void setDateCloture(Instant dateCloture) { this.dateCloture = dateCloture; }
    public String getMotifContestation() { return motifContestation; }
    public void setMotifContestation(String motifContestation) { this.motifContestation = motifContestation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}