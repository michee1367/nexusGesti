package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_bulletin", schema = "pedagogie")
public class LigneBulletinEntity {

    @Id
    private UUID id;

    @Column(name = "bulletin_id", nullable = false)
    private UUID bulletinId;

    @Column(nullable = false)
    private String niveau;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(nullable = false)
    private BigDecimal coefficient;

    @Column(name = "poids_normalise")
    private BigDecimal poidsNormalise;

    @Column(name = "moyenne_periode")
    private BigDecimal moyennePeriode;

    @Column(name = "moyenne_annuelle")
    private BigDecimal moyenneAnnuelle;

    @Column(name = "moyenne_ue")
    private BigDecimal moyenneUe;

    @Column(name = "moyenne_cours")
    private BigDecimal moyenneCours;

    private Integer rang;

    private Integer effectif;

    private String mention;

    private String appreciation;

    @Column(nullable = false)
    private boolean absent;

    @Column(name = "cas_particulier")
    private String casParticulier;

    @Column(name = "ue_id")
    private UUID ueId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "matiere_id")
    private UUID matiereId;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
