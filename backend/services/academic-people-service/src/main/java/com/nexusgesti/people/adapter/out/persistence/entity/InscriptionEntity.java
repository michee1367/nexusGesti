package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inscription", schema = "academic_people")
public class InscriptionEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(nullable = false)
    private boolean redoublant;

    @Column(nullable = false)
    private boolean nouveau;

    @Column(name = "ecole_origine", length = 160)
    private String ecoleOrigine;

    @Column(name = "moyenne_annee_precedente")
    private BigDecimal moyenneAnneePrecedente;

    @Column(name = "classement_annee_precedente")
    private Integer classementAnneePrecedente;

    @Column(name = "frais_regles", nullable = false)
    private boolean fraisRegles;

    @Column(name = "motif_annulation", length = 255)
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isRedoublant() { return redoublant; }
    public void setRedoublant(boolean redoublant) { this.redoublant = redoublant; }
    public boolean isNouveau() { return nouveau; }
    public void setNouveau(boolean nouveau) { this.nouveau = nouveau; }
    public String getEcoleOrigine() { return ecoleOrigine; }
    public void setEcoleOrigine(String ecoleOrigine) { this.ecoleOrigine = ecoleOrigine; }
    public BigDecimal getMoyenneAnneePrecedente() { return moyenneAnneePrecedente; }
    public void setMoyenneAnneePrecedente(BigDecimal moyenneAnneePrecedente) { this.moyenneAnneePrecedente = moyenneAnneePrecedente; }
    public Integer getClassementAnneePrecedente() { return classementAnneePrecedente; }
    public void setClassementAnneePrecedente(Integer classementAnneePrecedente) { this.classementAnneePrecedente = classementAnneePrecedente; }
    public boolean isFraisRegles() { return fraisRegles; }
    public void setFraisRegles(boolean fraisRegles) { this.fraisRegles = fraisRegles; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
