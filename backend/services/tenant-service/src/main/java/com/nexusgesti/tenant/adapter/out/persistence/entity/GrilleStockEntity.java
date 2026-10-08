package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grille_stock", schema = "tenant")
public class GrilleStockEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false, length = 20)
    private String profil;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "date_activation")
    private Instant dateActivation;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean modifiable;

    @Column(name = "strategie_rotation_lot", nullable = false, length = 30)
    private String strategieRotationLot;

    @Column(name = "duree_quarantaine_jours")
    private Integer dureeQuarantaineJours;

    @Column(name = "seuil_alerte_peremption_jours")
    private Integer seuilAlertePeremptionJours;

    @Column(name = "tolerance_ecart_inventaire_pct")
    private BigDecimal toleranceEcartInventairePct;

    @Column(name = "delai_reappro_jours")
    private Integer delaiReapproJours;

    @Column(name = "seuil_alerte_taux_rotation_pct")
    private BigDecimal seuilAlerteTauxRotationPct;

    @Column(name = "autoriser_peremption_en_sortie", nullable = false)
    private boolean autoriserPeremptionEnSortie;

    @Column(name = "exiger_controle_reception", nullable = false)
    private boolean exigerControleReception;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public String getStrategieRotationLot() { return strategieRotationLot; }
    public void setStrategieRotationLot(String strategieRotationLot) { this.strategieRotationLot = strategieRotationLot; }
    public Integer getDureeQuarantaineJours() { return dureeQuarantaineJours; }
    public void setDureeQuarantaineJours(Integer dureeQuarantaineJours) { this.dureeQuarantaineJours = dureeQuarantaineJours; }
    public Integer getSeuilAlertePeremptionJours() { return seuilAlertePeremptionJours; }
    public void setSeuilAlertePeremptionJours(Integer seuilAlertePeremptionJours) { this.seuilAlertePeremptionJours = seuilAlertePeremptionJours; }
    public BigDecimal getToleranceEcartInventairePct() { return toleranceEcartInventairePct; }
    public void setToleranceEcartInventairePct(BigDecimal toleranceEcartInventairePct) { this.toleranceEcartInventairePct = toleranceEcartInventairePct; }
    public Integer getDelaiReapproJours() { return delaiReapproJours; }
    public void setDelaiReapproJours(Integer delaiReapproJours) { this.delaiReapproJours = delaiReapproJours; }
    public BigDecimal getSeuilAlerteTauxRotationPct() { return seuilAlerteTauxRotationPct; }
    public void setSeuilAlerteTauxRotationPct(BigDecimal seuilAlerteTauxRotationPct) { this.seuilAlerteTauxRotationPct = seuilAlerteTauxRotationPct; }
    public boolean isAutoriserPeremptionEnSortie() { return autoriserPeremptionEnSortie; }
    public void setAutoriserPeremptionEnSortie(boolean autoriserPeremptionEnSortie) { this.autoriserPeremptionEnSortie = autoriserPeremptionEnSortie; }
    public boolean isExigerControleReception() { return exigerControleReception; }
    public void setExigerControleReception(boolean exigerControleReception) { this.exigerControleReception = exigerControleReception; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}