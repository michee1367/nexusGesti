package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seuil_decision", schema = "tenant")
public class SeuilDecisionEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 80)
    private String libelle;

    @Column(nullable = false, length = 30)
    private String portee;

    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(length = 60)
    private String niveau;

    @Column(length = 60)
    private String parcours;

    @Column(length = 60)
    private String programme;

    @Column(name = "moyenne_minimale")
    private BigDecimal moyenneMinimale;

    @Column(name = "moyenne_maximale")
    private BigDecimal moyenneMaximale;

    @Column(name = "taux_reussite_minimale")
    private BigDecimal tauxReussiteMinimale;

    @Column(name = "moyenne_de_reussite")
    private BigDecimal moyenneDeReussite;

    @Column(name = "absences_maximales")
    private Integer absencesMaximales;

    @Column(name = "decision_resultante", nullable = false, length = 20)
    private String decisionResultante;

    @Column(name = "rang_min")
    private Integer rangMin;

    @Column(name = "rang_max")
    private Integer rangMax;

    @Column(nullable = false)
    private int priorite;

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
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public BigDecimal getMoyenneMinimale() { return moyenneMinimale; }
    public void setMoyenneMinimale(BigDecimal moyenneMinimale) { this.moyenneMinimale = moyenneMinimale; }
    public BigDecimal getMoyenneMaximale() { return moyenneMaximale; }
    public void setMoyenneMaximale(BigDecimal moyenneMaximale) { this.moyenneMaximale = moyenneMaximale; }
    public BigDecimal getTauxReussiteMinimale() { return tauxReussiteMinimale; }
    public void setTauxReussiteMinimale(BigDecimal tauxReussiteMinimale) { this.tauxReussiteMinimale = tauxReussiteMinimale; }
    public BigDecimal getMoyenneDeReussite() { return moyenneDeReussite; }
    public void setMoyenneDeReussite(BigDecimal moyenneDeReussite) { this.moyenneDeReussite = moyenneDeReussite; }
    public Integer getAbsencesMaximales() { return absencesMaximales; }
    public void setAbsencesMaximales(Integer absencesMaximales) { this.absencesMaximales = absencesMaximales; }
    public String getDecisionResultante() { return decisionResultante; }
    public void setDecisionResultante(String decisionResultante) { this.decisionResultante = decisionResultante; }
    public Integer getRangMin() { return rangMin; }
    public void setRangMin(Integer rangMin) { this.rangMin = rangMin; }
    public Integer getRangMax() { return rangMax; }
    public void setRangMax(Integer rangMax) { this.rangMax = rangMax; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}