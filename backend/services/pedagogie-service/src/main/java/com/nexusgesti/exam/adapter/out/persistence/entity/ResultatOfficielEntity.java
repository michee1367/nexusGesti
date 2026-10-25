package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resultat_officiel", schema = "pedagogie")
public class ResultatOfficielEntity {

    @Id
    private UUID id;

    @Column(name = "decision_id")
    private UUID decisionId;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private boolean publie;

    @Column(name = "date_publication")
    private Instant datePublication;

    private UUID generateur;

    @Column(name = "url_document")
    private String urlDocument;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDecisionId() { return decisionId; }
    public void setDecisionId(UUID decisionId) { this.decisionId = decisionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public Instant getDatePublication() { return datePublication; }
    public void setDatePublication(Instant datePublication) { this.datePublication = datePublication; }
    public UUID getGenerateur() { return generateur; }
    public void setGenerateur(UUID generateur) { this.generateur = generateur; }
    public String getUrlDocument() { return urlDocument; }
    public void setUrlDocument(String urlDocument) { this.urlDocument = urlDocument; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}