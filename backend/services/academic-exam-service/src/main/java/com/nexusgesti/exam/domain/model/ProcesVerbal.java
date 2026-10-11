package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcesVerbal {

    private UUID id;
    private UUID sessionId;
    private String reference;
    private StatutPV statut = StatutPV.BROUILLON;
    private LocalDate dateRedaction;
    private UUID redigePar;
    private boolean presenceDirecteur;
    private String observations;
    private int nbAbsences;
    private int nbIncidents;
    private Instant dateCloture;
    private String motifContestation;

    private List<Signature> signatures = new ArrayList<>();

    public ProcesVerbal rediger(UUID par) {
        this.statut = StatutPV.BROUILLON;
        this.redigePar = par;
        if (this.dateRedaction == null) {
            this.dateRedaction = LocalDate.now();
        }
        return this;
    }

    public ProcesVerbal soumettre() {
        if (statut != StatutPV.BROUILLON) {
            throw ExamException.transitionInvalide(statut.name(), StatutPV.SOUMIS.name());
        }
        this.statut = StatutPV.SOUMIS;
        return this;
    }

    public ProcesVerbal signer(UUID utilisateur, String role) {
        if (statut == StatutPV.HOMOLOGUE) {
            throw ExamException.transitionInvalide(statut.name(), StatutPV.SIGNE.name());
        }
        Signature signature = new Signature();
        signature.setId(UUID.randomUUID());
        signature.setProcesVerbalId(id);
        signature.setSignataire(utilisateur);
        signature.setRole(role);
        signature.setDateSignature(Instant.now());
        signature.setValidee(true);
        signature.setEmpreinte(Integer.toHexString((reference + "|" + utilisateur + "|" + role).hashCode()));
        this.signatures.add(signature);
        this.statut = StatutPV.SIGNE;
        return this;
    }

    public ProcesVerbal homologuer() {
        if (statut != StatutPV.SIGNE) {
            throw ExamException.transitionInvalide(statut.name(), StatutPV.HOMOLOGUE.name());
        }
        this.statut = StatutPV.HOMOLOGUE;
        this.dateCloture = Instant.now();
        return this;
    }

    public ProcesVerbal contester(String motif) {
        if (motif == null || motif.isBlank()) {
            throw ExamException.invalide("Le motif de contestation est obligatoire");
        }
        this.statut = StatutPV.CONTESTE;
        this.motifContestation = motif;
        return this;
    }

    public boolean estComplet() {
        return !signatures.isEmpty()
                && signatures.stream().anyMatch(s -> "DIRECTEUR".equals(s.getRole()))
                && signatures.stream().anyMatch(s -> "SECRETAIRE".equals(s.getRole()));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public StatutPV getStatut() { return statut; }
    public void setStatut(StatutPV statut) { this.statut = statut; }
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
    public List<Signature> getSignatures() { return signatures; }
    public void setSignatures(List<Signature> signatures) { this.signatures = signatures == null ? new ArrayList<>() : new ArrayList<>(signatures); }
}
