package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bulletin {

    private UUID idBulletin;
    private String reference;
    private UUID eleveId;
    private UUID anneeId;
    private UUID periodeId;
    private UUID classeId;
    private TypeBulletin type;
    private StatutBulletin statut;
    private Integer version = 1;
    private UUID grilleId;
    private Integer grilleVersion;
    private LocalDate grilleDateActivation;
    private BigDecimal moyenneGenerale;
    private BigDecimal moyennePeriode;
    private BigDecimal moyenneAnnuelle;
    private Integer rang;
    private Integer effectif;
    private String appreciationGenerale;
    private String mentionGenerale;
    private String decision;
    private int nbAbsencesJustifiees;
    private int nbAbsencesInjustifiees;
    private int nbRetards;
    private BigDecimal tauxPresence;
    private String regleAssiduite;
    private Boolean respecteRegleAssiduite;
    private String avertissementAssiduite;
    private Integer retardJours;
    private Integer retardHeures;
    private Instant genereLe;
    private Instant publieLe;
    private UUID generePar;
    private Instant rectifieLe;
    private String motifRectification;
    private UUID creePar;

    private List<LigneBulletin> lignes = new ArrayList<>();
    private List<CouponNotes> coupons = new ArrayList<>();

    public Bulletin() {
    }

    public Bulletin calculer() {
        if (!estEditable()) {
            throw AssessmentException.etatInvalide("Un bulletin " + statut + " n est plus calculable");
        }
        BigDecimal totalCoefficients = BigDecimal.ZERO;
        BigDecimal sommePonderee = BigDecimal.ZERO;
        for (LigneBulletin ligne : lignes) {
            if (ligne.compteDansMoyenne()) {
                totalCoefficients = totalCoefficients.add(ligne.getCoefficient());
                sommePonderee = sommePonderee.add(ligne.getContribution());
            }
        }
        BigDecimal totalPourNormalisation = totalCoefficients;
        lignes.forEach(l -> l.normaliser(totalPourNormalisation));
        if (totalCoefficients.signum() > 0) {
            this.moyenneGenerale = sommePonderee.divide(totalCoefficients, 2, RoundingMode.HALF_UP);
            this.moyennePeriode = this.moyenneGenerale;
        } else {
            this.moyenneGenerale = null;
            this.moyennePeriode = null;
        }
        this.statut = StatutBulletin.CALCULE;
        return this;
    }

    public Bulletin publier() {
        if (statut != StatutBulletin.CALCULE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutBulletin.PUBLIE.name());
        }
        this.statut = StatutBulletin.PUBLIE;
        this.publieLe = Instant.now();
        return this;
    }

    public Bulletin genererDocuments() {
        if (statut != StatutBulletin.PUBLIE && statut != StatutBulletin.RECTIFIE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutBulletin.GENERE.name());
        }
        this.statut = StatutBulletin.GENERE;
        this.genereLe = Instant.now();
        return this;
    }

    public Bulletin signer(UUID par) {
        if (statut != StatutBulletin.GENERE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutBulletin.SIGNE.name());
        }
        this.statut = StatutBulletin.SIGNE;
        this.generePar = par;
        return this;
    }

    public Bulletin rectifier(String motif) {
        if (motif == null || motif.isBlank()) {
            throw AssessmentException.invalide("Le motif de rectification est obligatoire");
        }
        if (statut != StatutBulletin.PUBLIE && statut != StatutBulletin.GENERE && statut != StatutBulletin.SIGNE) {
            throw AssessmentException.etatInvalide("Un bulletin " + statut + " ne peut pas etre rectifie");
        }
        this.statut = StatutBulletin.RECTIFIE;
        this.version = (version == null ? 1 : version) + 1;
        this.motifRectification = motif;
        this.rectifieLe = Instant.now();
        return this;
    }

    public boolean estEditable() {
        return statut == null || statut == StatutBulletin.BROUILLON || statut == StatutBulletin.CALCULE;
    }

    public LigneBulletin ajouterLigne(LigneBulletin ligne) {
        if (!estEditable()) {
            throw AssessmentException.etatInvalide("Un bulletin " + statut + " n est plus modifiable");
        }
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        ligne.setBulletinId(idBulletin);
        lignes.add(ligne);
        return ligne;
    }

    public void definirSyntheseAbsences(SyntheseAbsences synthese) {
        if (synthese == null) {
            return;
        }
        this.nbAbsencesJustifiees = synthese.getSeancesJustifiees();
        this.nbAbsencesInjustifiees = synthese.getSeancesInjustifiees();
        this.nbRetards = synthese.getRetardJours() + synthese.getRetardHeures();
        this.retardJours = synthese.getRetardJours();
        this.retardHeures = synthese.getRetardHeures();
        this.regleAssiduite = synthese.getRegleApplicable();
        this.respecteRegleAssiduite = synthese.isRespecteRegle();
        this.avertissementAssiduite = synthese.getAvertissement();
    }

    public SyntheseAbsences getSyntheseAbsences() {
        SyntheseAbsences s = new SyntheseAbsences();
        s.setSeancesJustifiees(nbAbsencesJustifiees);
        s.setSeancesInjustifiees(nbAbsencesInjustifiees);
        s.setRetardJours(retardJours == null ? 0 : retardJours);
        s.setRetardHeures(retardHeures == null ? 0 : retardHeures);
        s.setRegleApplicable(regleAssiduite);
        s.setRespecteRegle(Boolean.TRUE.equals(respecteRegleAssiduite));
        s.setAvertissement(avertissementAssiduite);
        return s;
    }

    public UUID getIdBulletin() { return idBulletin; }
    public void setIdBulletin(UUID idBulletin) { this.idBulletin = idBulletin; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public TypeBulletin getType() { return type; }
    public void setType(TypeBulletin type) { this.type = type; }
    public StatutBulletin getStatut() { return statut; }
    public void setStatut(StatutBulletin statut) { this.statut = statut; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public Integer getGrilleVersion() { return grilleVersion; }
    public void setGrilleVersion(Integer grilleVersion) { this.grilleVersion = grilleVersion; }
    public LocalDate getGrilleDateActivation() { return grilleDateActivation; }
    public void setGrilleDateActivation(LocalDate grilleDateActivation) { this.grilleDateActivation = grilleDateActivation; }
    public BigDecimal getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(BigDecimal moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
    public BigDecimal getMoyennePeriode() { return moyennePeriode; }
    public void setMoyennePeriode(BigDecimal moyennePeriode) { this.moyennePeriode = moyennePeriode; }
    public BigDecimal getMoyenneAnnuelle() { return moyenneAnnuelle; }
    public void setMoyenneAnnuelle(BigDecimal moyenneAnnuelle) { this.moyenneAnnuelle = moyenneAnnuelle; }
    public Integer getRang() { return rang; }
    public void setRang(Integer rang) { this.rang = rang; }
    public Integer getEffectif() { return effectif; }
    public void setEffectif(Integer effectif) { this.effectif = effectif; }
    public String getAppreciationGenerale() { return appreciationGenerale; }
    public void setAppreciationGenerale(String appreciationGenerale) { this.appreciationGenerale = appreciationGenerale; }
    public String getMentionGenerale() { return mentionGenerale; }
    public void setMentionGenerale(String mentionGenerale) { this.mentionGenerale = mentionGenerale; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public int getNbAbsencesJustifiees() { return nbAbsencesJustifiees; }
    public void setNbAbsencesJustifiees(int nbAbsencesJustifiees) { this.nbAbsencesJustifiees = nbAbsencesJustifiees; }
    public int getNbAbsencesInjustifiees() { return nbAbsencesInjustifiees; }
    public void setNbAbsencesInjustifiees(int nbAbsencesInjustifiees) { this.nbAbsencesInjustifiees = nbAbsencesInjustifiees; }
    public int getNbRetards() { return nbRetards; }
    public void setNbRetards(int nbRetards) { this.nbRetards = nbRetards; }
    public BigDecimal getTauxPresence() { return tauxPresence; }
    public void setTauxPresence(BigDecimal tauxPresence) { this.tauxPresence = tauxPresence; }
    public String getRegleAssiduite() { return regleAssiduite; }
    public void setRegleAssiduite(String regleAssiduite) { this.regleAssiduite = regleAssiduite; }
    public Boolean getRespecteRegleAssiduite() { return respecteRegleAssiduite; }
    public void setRespecteRegleAssiduite(Boolean respecteRegleAssiduite) { this.respecteRegleAssiduite = respecteRegleAssiduite; }
    public String getAvertissementAssiduite() { return avertissementAssiduite; }
    public void setAvertissementAssiduite(String avertissementAssiduite) { this.avertissementAssiduite = avertissementAssiduite; }
    public Integer getRetardJours() { return retardJours; }
    public void setRetardJours(Integer retardJours) { this.retardJours = retardJours; }
    public Integer getRetardHeures() { return retardHeures; }
    public void setRetardHeures(Integer retardHeures) { this.retardHeures = retardHeures; }
    public Instant getGenereLe() { return genereLe; }
    public void setGenereLe(Instant genereLe) { this.genereLe = genereLe; }
    public Instant getPublieLe() { return publieLe; }
    public void setPublieLe(Instant publieLe) { this.publieLe = publieLe; }
    public UUID getGenerePar() { return generePar; }
    public void setGenerePar(UUID generePar) { this.generePar = generePar; }
    public Instant getRectifieLe() { return rectifieLe; }
    public void setRectifieLe(Instant rectifieLe) { this.rectifieLe = rectifieLe; }
    public String getMotifRectification() { return motifRectification; }
    public void setMotifRectification(String motifRectification) { this.motifRectification = motifRectification; }
    public UUID getCreePar() { return creePar; }
    public void setCreePar(UUID creePar) { this.creePar = creePar; }
    public List<LigneBulletin> getLignes() { return lignes; }
    public void setLignes(List<LigneBulletin> lignes) { this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes); }
    public List<CouponNotes> getCoupons() { return coupons; }
    public void setCoupons(List<CouponNotes> coupons) { this.coupons = coupons == null ? new ArrayList<>() : new ArrayList<>(coupons); }
}
