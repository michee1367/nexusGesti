package com.nexusgesti.assessment.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class LigneBulletin {

    private UUID id;
    private UUID bulletinId;
    private String niveau;
    private String code;
    private String libelle;
    private String parentCode;
    private BigDecimal coefficient = BigDecimal.ONE;
    private BigDecimal poidsNormalise;
    private BigDecimal moyennePeriode;
    private BigDecimal moyenneAnnuelle;
    private BigDecimal moyenneUe;
    private BigDecimal moyenneCours;
    private Integer rang;
    private Integer effectif;
    private String mention;
    private String appreciation;
    private boolean absent;
    private String casParticulier;
    private UUID ueId;
    private UUID coursId;
    private UUID matiereId;

    public LigneBulletin() {
    }

    public LigneBulletin(String niveau, String code, String libelle, BigDecimal coefficient) {
        this.niveau = niveau;
        this.code = code;
        this.libelle = libelle;
        if (coefficient != null) {
            this.coefficient = coefficient;
        }
    }

    public boolean estReussi(BigDecimal seuil) {
        return moyennePeriode != null && seuil != null && moyennePeriode.compareTo(seuil) >= 0;
    }

    public BigDecimal getContribution() {
        if (moyennePeriode == null || coefficient == null) {
            return BigDecimal.ZERO;
        }
        return moyennePeriode.multiply(coefficient);
    }

    public boolean compteDansMoyenne() {
        return !absent && moyennePeriode != null && coefficient != null;
    }

    public BigDecimal normaliser(BigDecimal totalCoefficients) {
        if (totalCoefficients == null || totalCoefficients.signum() == 0 || coefficient == null) {
            this.poidsNormalise = BigDecimal.ZERO;
            return this.poidsNormalise;
        }
        this.poidsNormalise = coefficient.divide(totalCoefficients, 4, RoundingMode.HALF_UP);
        return this.poidsNormalise;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBulletinId() { return bulletinId; }
    public void setBulletinId(UUID bulletinId) { this.bulletinId = bulletinId; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public BigDecimal getPoidsNormalise() { return poidsNormalise; }
    public void setPoidsNormalise(BigDecimal poidsNormalise) { this.poidsNormalise = poidsNormalise; }
    public BigDecimal getMoyennePeriode() { return moyennePeriode; }
    public void setMoyennePeriode(BigDecimal moyennePeriode) { this.moyennePeriode = moyennePeriode; }
    public BigDecimal getMoyenneAnnuelle() { return moyenneAnnuelle; }
    public void setMoyenneAnnuelle(BigDecimal moyenneAnnuelle) { this.moyenneAnnuelle = moyenneAnnuelle; }
    public BigDecimal getMoyenneUe() { return moyenneUe; }
    public void setMoyenneUe(BigDecimal moyenneUe) { this.moyenneUe = moyenneUe; }
    public BigDecimal getMoyenneCours() { return moyenneCours; }
    public void setMoyenneCours(BigDecimal moyenneCours) { this.moyenneCours = moyenneCours; }
    public Integer getRang() { return rang; }
    public void setRang(Integer rang) { this.rang = rang; }
    public Integer getEffectif() { return effectif; }
    public void setEffectif(Integer effectif) { this.effectif = effectif; }
    public String getMention() { return mention; }
    public void setMention(String mention) { this.mention = mention; }
    public String getAppreciation() { return appreciation; }
    public void setAppreciation(String appreciation) { this.appreciation = appreciation; }
    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }
    public String getCasParticulier() { return casParticulier; }
    public void setCasParticulier(String casParticulier) { this.casParticulier = casParticulier; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getMatiereId() { return matiereId; }
    public void setMatiereId(UUID matiereId) { this.matiereId = matiereId; }
}
