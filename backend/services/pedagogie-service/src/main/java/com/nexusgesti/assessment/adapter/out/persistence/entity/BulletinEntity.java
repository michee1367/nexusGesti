package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bulletin", schema = "pedagogie")
public class BulletinEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String reference;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "periode_id")
    private UUID periodeId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String statut;

    @Column(nullable = false)
    private int version;

    @Column(name = "grille_id")
    private UUID grilleId;

    @Column(name = "grille_version")
    private Integer grilleVersion;

    @Column(name = "grille_date_activation")
    private LocalDate grilleDateActivation;

    @Column(name = "moyenne_generale")
    private BigDecimal moyenneGenerale;

    @Column(name = "moyenne_periode")
    private BigDecimal moyennePeriode;

    @Column(name = "moyenne_annuelle")
    private BigDecimal moyenneAnnuelle;

    private Integer rang;

    private Integer effectif;

    @Column(name = "appreciation_generale")
    private String appreciationGenerale;

    @Column(name = "mention_generale")
    private String mentionGenerale;

    private String decision;

    @Column(name = "nb_absences_justifiees", nullable = false)
    private int nbAbsencesJustifiees;

    @Column(name = "nb_absences_injustifiees", nullable = false)
    private int nbAbsencesInjustifiees;

    @Column(name = "nb_retards", nullable = false)
    private int nbRetards;

    @Column(name = "taux_presence")
    private BigDecimal tauxPresence;

    @Column(name = "regle_assiduite")
    private String regleAssiduite;

    @Column(name = "respecte_regle_assiduite")
    private Boolean respecteRegleAssiduite;

    @Column(name = "avertissement_assiduite")
    private String avertissementAssiduite;

    @Column(name = "retard_jours")
    private Integer retardJours;

    @Column(name = "retard_heures")
    private Integer retardHeures;

    @Column(name = "genere_le")
    private Instant genereLe;

    @Column(name = "publie_le")
    private Instant publieLe;

    @Column(name = "genere_par")
    private UUID generePar;

    @Column(name = "rectifie_le")
    private Instant rectifieLe;

    @Column(name = "motif_rectification")
    private String motifRectification;

    @Column(name = "cree_par")
    private UUID creePar;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
