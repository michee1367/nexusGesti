package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "correction", schema = "pedagogie")
public class CorrectionEntity {

    @Id
    private UUID id;

    @Column(name = "epreuve_id", nullable = false)
    private UUID epreuveId;

    private UUID correcteur;

    @Column(name = "date_correction")
    private LocalDate dateCorrection;

    @Column(nullable = false)
    private String statut;

    @Column(name = "nb_notes_corrigees", nullable = false)
    private int nbNotesCorrigees;

    @Column(name = "moyenne_brute")
    private BigDecimal moyenneBrute;

    @Column(name = "moyenne_retenue")
    private BigDecimal moyenneRetenue;

    @Column(name = "plus_forte_moyenne")
    private BigDecimal plusForteMoyenne;

    @Column(name = "plus_faible_moyenne")
    private BigDecimal plusFaibleMoyenne;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public UUID getCorrecteur() { return correcteur; }
    public void setCorrecteur(UUID correcteur) { this.correcteur = correcteur; }
    public LocalDate getDateCorrection() { return dateCorrection; }
    public void setDateCorrection(LocalDate dateCorrection) { this.dateCorrection = dateCorrection; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getNbNotesCorrigees() { return nbNotesCorrigees; }
    public void setNbNotesCorrigees(int nbNotesCorrigees) { this.nbNotesCorrigees = nbNotesCorrigees; }
    public BigDecimal getMoyenneBrute() { return moyenneBrute; }
    public void setMoyenneBrute(BigDecimal moyenneBrute) { this.moyenneBrute = moyenneBrute; }
    public BigDecimal getMoyenneRetenue() { return moyenneRetenue; }
    public void setMoyenneRetenue(BigDecimal moyenneRetenue) { this.moyenneRetenue = moyenneRetenue; }
    public BigDecimal getPlusForteMoyenne() { return plusForteMoyenne; }
    public void setPlusForteMoyenne(BigDecimal plusForteMoyenne) { this.plusForteMoyenne = plusForteMoyenne; }
    public BigDecimal getPlusFaibleMoyenne() { return plusFaibleMoyenne; }
    public void setPlusFaibleMoyenne(BigDecimal plusFaibleMoyenne) { this.plusFaibleMoyenne = plusFaibleMoyenne; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}