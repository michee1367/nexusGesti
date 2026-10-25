package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regle_assiduite", schema = "socle")
public class RegleAssiduiteEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 30)
    private String portee;

    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(length = 60)
    private String niveau;

    @Column(name = "absences_max_par_periode")
    private Integer absencesMaxParPeriode;

    @Column(name = "absences_par_jour_max")
    private Integer absencesParJourMax;

    @Column(name = "taux_presence_minimale")
    private BigDecimal tauxPresenceMinimale;

    @Column(name = "absences_justifiees_exonerees", nullable = false)
    private boolean absencesJustifieesExonerees;

    @Column(name = "retard_compte_comme_absence", nullable = false)
    private boolean retardCompteCommeAbsence;

    @Column(name = "seuil_retard_minutes")
    private Integer seuilRetardMinutes;

    @Column(length = 20)
    private String consequence;

    @Column(nullable = false)
    private int priorite;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getPortee() { return portee; }
    public void setPortee(String portee) { this.portee = portee; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public Integer getAbsencesMaxParPeriode() { return absencesMaxParPeriode; }
    public void setAbsencesMaxParPeriode(Integer absencesMaxParPeriode) { this.absencesMaxParPeriode = absencesMaxParPeriode; }
    public Integer getAbsencesParJourMax() { return absencesParJourMax; }
    public void setAbsencesParJourMax(Integer absencesParJourMax) { this.absencesParJourMax = absencesParJourMax; }
    public BigDecimal getTauxPresenceMinimale() { return tauxPresenceMinimale; }
    public void setTauxPresenceMinimale(BigDecimal tauxPresenceMinimale) { this.tauxPresenceMinimale = tauxPresenceMinimale; }
    public boolean isAbsencesJustifieesExonerees() { return absencesJustifieesExonerees; }
    public void setAbsencesJustifieesExonerees(boolean absencesJustifieesExonerees) { this.absencesJustifieesExonerees = absencesJustifieesExonerees; }
    public boolean isRetardCompteCommeAbsence() { return retardCompteCommeAbsence; }
    public void setRetardCompteCommeAbsence(boolean retardCompteCommeAbsence) { this.retardCompteCommeAbsence = retardCompteCommeAbsence; }
    public Integer getSeuilRetardMinutes() { return seuilRetardMinutes; }
    public void setSeuilRetardMinutes(Integer seuilRetardMinutes) { this.seuilRetardMinutes = seuilRetardMinutes; }
    public String getConsequence() { return consequence; }
    public void setConsequence(String consequence) { this.consequence = consequence; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}