package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "deliberation", schema = "pedagogie")
public class DeliberationEntity {

    @Id
    private UUID id;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(nullable = false)
    private String reference;

    @Column(name = "date_deliberation")
    private LocalDate dateDeliberation;

    @Column(nullable = false)
    private String statut;

    private UUID president;

    @JdbcTypeCode(SqlTypes.JSON)
    private String membres;

    @Column(name = "quorum_atteint", nullable = false)
    private boolean quorumAtteint;

    @Column(name = "grille_version")
    private Integer grilleVersion;

    @Column(name = "date_cloture")
    private Instant dateCloture;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDate getDateDeliberation() { return dateDeliberation; }
    public void setDateDeliberation(LocalDate dateDeliberation) { this.dateDeliberation = dateDeliberation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getPresident() { return president; }
    public void setPresident(UUID president) { this.president = president; }
    public String getMembres() { return membres; }
    public void setMembres(String membres) { this.membres = membres; }
    public boolean isQuorumAtteint() { return quorumAtteint; }
    public void setQuorumAtteint(boolean quorumAtteint) { this.quorumAtteint = quorumAtteint; }
    public Integer getGrilleVersion() { return grilleVersion; }
    public void setGrilleVersion(Integer grilleVersion) { this.grilleVersion = grilleVersion; }
    public Instant getDateCloture() { return dateCloture; }
    public void setDateCloture(Instant dateCloture) { this.dateCloture = dateCloture; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}