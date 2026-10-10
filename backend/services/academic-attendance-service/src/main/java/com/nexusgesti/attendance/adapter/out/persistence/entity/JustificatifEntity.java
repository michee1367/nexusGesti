package com.nexusgesti.attendance.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "justificatif", schema = "academic_attendance")
public class JustificatifEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    private String type;

    @Column(name = "date_reference")
    private LocalDate dateReference;

    @Column(name = "date_depot")
    private LocalDate dateDepot;

    private String motif;

    @Column(name = "fichier_uri")
    private String fichierUri;

    @Column(nullable = false)
    private String statut;

    @Column(name = "traite_par")
    private UUID traitePar;

    @Column(name = "traite_le")
    private Instant traiteLe;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDateReference() { return dateReference; }
    public void setDateReference(LocalDate dateReference) { this.dateReference = dateReference; }
    public LocalDate getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDate dateDepot) { this.dateDepot = dateDepot; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getFichierUri() { return fichierUri; }
    public void setFichierUri(String fichierUri) { this.fichierUri = fichierUri; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTraitePar() { return traitePar; }
    public void setTraitePar(UUID traitePar) { this.traitePar = traitePar; }
    public Instant getTraiteLe() { return traiteLe; }
    public void setTraiteLe(Instant traiteLe) { this.traiteLe = traiteLe; }
    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
