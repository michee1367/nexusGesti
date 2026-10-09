package com.nexusgesti.identity.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "motif_desactivation", schema = "identity")
public class MotifDesactivationEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "utilisateur_id", nullable = false)
    private UUID utilisateurId;

    @Column(nullable = false)
    private Instant date;

    @Column(nullable = false, length = 255)
    private String motif;

    private UUID decideur;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public UUID getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getDecideur() { return decideur; }
    public void setDecideur(UUID decideur) { this.decideur = decideur; }
}
