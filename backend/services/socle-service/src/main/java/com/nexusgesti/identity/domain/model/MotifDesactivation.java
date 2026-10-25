package com.nexusgesti.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public class MotifDesactivation {
    private UUID id;
    private Instant date;
    private String motif;
    private UUID decideur;

    public MotifDesactivation() {
    }

    public MotifDesactivation(String motif, UUID decideur) {
        this.id = UUID.randomUUID();
        this.date = Instant.now();
        this.motif = motif;
        this.decideur = decideur;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getDecideur() { return decideur; }
    public void setDecideur(UUID decideur) { this.decideur = decideur; }
}
