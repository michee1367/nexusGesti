package com.nexusgesti.enrollment.domain.model;

import com.nexusgesti.enrollment.domain.EnrollmentException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class InscriptionUE {

    private UUID idInscription;
    private UUID eleveId;
    private UUID programmeId;
    private UUID promotionId;
    private UUID ueId;
    private UUID anneeId;
    private UUID semestreId;
    private StatutInscriptionUE statut;
    private LocalDate dateInscription;
    private BigDecimal creditsReconnus;
    private BigDecimal moyenneAnneePrecedente;
    private BigDecimal moyenneUEPrecedente;
    private String option;
    private String motifRefus;
    private Integer rangListeAttente;
    private Instant valideLe;
    private UUID validePar;

    private static final Set<StatutInscriptionUE> VALIDABLES =
            Set.of(StatutInscriptionUE.EN_ATTENTE, StatutInscriptionUE.INSCRIT,
                    StatutInscriptionUE.EN_LISTE_ATTENTE, StatutInscriptionUE.RATTAPE);

    public InscriptionUE inscrire() {
        if (statut != null && statut != StatutInscriptionUE.EN_ATTENTE) {
            throw EnrollmentException.transitionInvalide(statut.name(), StatutInscriptionUE.INSCRIT.name());
        }
        this.statut = StatutInscriptionUE.INSCRIT;
        if (this.dateInscription == null) {
            this.dateInscription = LocalDate.now();
        }
        return this;
    }

    public InscriptionUE placerEnListeAttente(int rang) {
        if (statut != null && statut != StatutInscriptionUE.EN_ATTENTE
                && statut != StatutInscriptionUE.INSCRIT) {
            throw EnrollmentException.transitionInvalide(statut.name(), StatutInscriptionUE.EN_LISTE_ATTENTE.name());
        }
        this.statut = StatutInscriptionUE.EN_LISTE_ATTENTE;
        this.rangListeAttente = rang;
        return this;
    }

    public InscriptionUE valider(UUID par) {
        if (!VALIDABLES.contains(statut)) {
            throw EnrollmentException.transitionInvalide(String.valueOf(statut), StatutInscriptionUE.VALIDE.name());
        }
        this.statut = StatutInscriptionUE.VALIDE;
        this.valideLe = Instant.now();
        this.validePar = par;
        this.rangListeAttente = null;
        return this;
    }

    public InscriptionUE refuser(String motif) {
        if (statut == StatutInscriptionUE.VALIDE || statut == StatutInscriptionUE.ANNULE
                || statut == StatutInscriptionUE.TRANSFERE) {
            throw EnrollmentException.transitionInvalide(String.valueOf(statut), StatutInscriptionUE.REFUSE.name());
        }
        this.statut = StatutInscriptionUE.REFUSE;
        this.motifRefus = motif;
        return this;
    }

    public InscriptionUE annuler() {
        if (statut == StatutInscriptionUE.VALIDE) {
            throw EnrollmentException.transitionInvalide(statut.name(), StatutInscriptionUE.ANNULE.name());
        }
        this.statut = StatutInscriptionUE.ANNULE;
        return this;
    }

    public boolean estRattape() {
        return statut == StatutInscriptionUE.RATTAPE;
    }

    public BigDecimal getCreditsAcquis() {
        return statut == StatutInscriptionUE.VALIDE ? nvl(creditsReconnus) : BigDecimal.ZERO;
    }

    public BigDecimal getCreditsRestants() {
        return statut == StatutInscriptionUE.VALIDE ? BigDecimal.ZERO : nvl(creditsReconnus);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public UUID getIdInscription() { return idInscription; }
    public void setIdInscription(UUID idInscription) { this.idInscription = idInscription; }
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
    public StatutInscriptionUE getStatut() { return statut; }
    public void setStatut(StatutInscriptionUE statut) { this.statut = statut; }
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
}
