package com.nexusgesti.enrollment.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inscription_ue", schema = "academic_enrollment")
public class InscriptionUEEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "programme_id")
    private UUID programmeId;

    @Column(name = "promotion_id")
    private UUID promotionId;

    @Column(name = "ue_id", nullable = false)
    private UUID ueId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "semestre_id")
    private UUID semestreId;

    @Column(nullable = false)
    private String statut;

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    @Column(name = "credits_reconnus")
    private BigDecimal creditsReconnus;

    @Column(name = "moyenne_annee_precedente")
    private BigDecimal moyenneAnneePrecedente;

    @Column(name = "moyenne_ue_precedente")
    private BigDecimal moyenneUEPrecedente;

    private String option;

    @Column(name = "motif_refus")
    private String motifRefus;

    @Column(name = "rang_liste_attente")
    private Integer rangListeAttente;

    @Column(name = "valide_le")
    private Instant valideLe;

    @Column(name = "valide_par")
    private UUID validePar;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public UUID getPromotionId() { return promotionId; }
    public void setPromotionId(UUID promotionId) { this.promotionId = promotionId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public BigDecimal getCreditsReconnus() { return creditsReconnus; }
    public void setCreditsReconnus(BigDecimal creditsReconnus) { this.creditsReconnus = creditsReconnus; }
    public BigDecimal getMoyenneAnneePrecedente() { return moyenneAnneePrecedente; }
    public void setMoyenneAnneePrecedente(BigDecimal moyenneAnneePrecedente) { this.moyenneAnneePrecedente = moyenneAnneePrecedente; }
    public BigDecimal getMoyenneUEPrecedente() { return moyenneUEPrecedente; }
    public void setMoyenneUEPrecedente(BigDecimal moyenneUEPrecedente) { this.moyenneUEPrecedente = moyenneUEPrecedente; }
    public String getOption() { return option; }
    public void setOption(String option) { this.option = option; }
    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String motifRefus) { this.motifRefus = motifRefus; }
    public Integer getRangListeAttente() { return rangListeAttente; }
    public void setRangListeAttente(Integer rangListeAttente) { this.rangListeAttente = rangListeAttente; }
    public Instant getValideLe() { return valideLe; }
    public void setValideLe(Instant valideLe) { this.valideLe = valideLe; }
    public UUID getValidePar() { return validePar; }
    public void setValidePar(UUID validePar) { this.validePar = validePar; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
