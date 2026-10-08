package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regle_ponderation", schema = "tenant")
public class ReglePonderationEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 30)
    private String portee;

    @Column(length = 20)
    private String profil;

    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(length = 60)
    private String niveau;

    @Column(length = 60)
    private String parcours;

    @Column(length = 60)
    private String programme;

    @Column(length = 60)
    private String semestre;

    @Column(length = 60)
    private String ue;

    @Column(length = 60)
    private String cours;

    @Column(length = 60)
    private String matiere;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "type_evaluation", length = 30)
    private String typeEvaluation;

    @Column(name = "type_session", length = 30)
    private String typeSession;

    @Column
    private BigDecimal coefficient;

    @Column(name = "seuil_inclusion")
    private BigDecimal seuilInclusion;

    @Column(name = "plafond_contribution")
    private BigDecimal plafondContribution;

    @Column(name = "poids_max")
    private BigDecimal poidsMax;

    @Column(nullable = false)
    private int priorite;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getPortee() { return portee; }
    public void setPortee(String portee) { this.portee = portee; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
    public String getUe() { return ue; }
    public void setUe(String ue) { this.ue = ue; }
    public String getCours() { return cours; }
    public void setCours(String cours) { this.cours = cours; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public String getTypeEvaluation() { return typeEvaluation; }
    public void setTypeEvaluation(String typeEvaluation) { this.typeEvaluation = typeEvaluation; }
    public String getTypeSession() { return typeSession; }
    public void setTypeSession(String typeSession) { this.typeSession = typeSession; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public BigDecimal getSeuilInclusion() { return seuilInclusion; }
    public void setSeuilInclusion(BigDecimal seuilInclusion) { this.seuilInclusion = seuilInclusion; }
    public BigDecimal getPlafondContribution() { return plafondContribution; }
    public void setPlafondContribution(BigDecimal plafondContribution) { this.plafondContribution = plafondContribution; }
    public BigDecimal getPoidsMax() { return poidsMax; }
    public void setPoidsMax(BigDecimal poidsMax) { this.poidsMax = poidsMax; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}