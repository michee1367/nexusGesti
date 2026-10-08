package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrilleStock extends Grille {

    private StrategieRotationLot strategieRotationLot;
    private Integer dureeQuarantaineJours;
    private Integer seuilAlertePeremptionJours;
    private BigDecimal toleranceEcartInventairePct;
    private Integer delaiReapproJours;
    private BigDecimal seuilAlerteTauxRotationPct;
    private boolean autoriserPeremptionEnSortie;
    private boolean exigerControleReception;
    private List<AlerteStock> alertes = new ArrayList<>();

    public int getDureeQuarantaine() {
        return dureeQuarantaineJours == null ? 0 : dureeQuarantaineJours;
    }

    public int getSeuilAlertePeremption() {
        return seuilAlertePeremptionJours == null ? 0 : seuilAlertePeremptionJours;
    }

    public int getDelaiReappro() {
        return delaiReapproJours == null ? 0 : delaiReapproJours;
    }

    public boolean estEcartInventaireTolere(BigDecimal ecartPct) {
        if (toleranceEcartInventairePct == null || ecartPct == null) {
            return false;
        }
        return ecartPct.compareTo(toleranceEcartInventairePct) <= 0;
    }

    public BigDecimal getToleranceInventaire() {
        return toleranceEcartInventairePct == null ? BigDecimal.ZERO : toleranceEcartInventairePct;
    }

    public GrilleStock nouvelleVersion() {
        GrilleStock copie = new GrilleStock();
        copierCommun(copie);
        copie.strategieRotationLot = this.strategieRotationLot;
        copie.dureeQuarantaineJours = this.dureeQuarantaineJours;
        copie.seuilAlertePeremptionJours = this.seuilAlertePeremptionJours;
        copie.toleranceEcartInventairePct = this.toleranceEcartInventairePct;
        copie.delaiReapproJours = this.delaiReapproJours;
        copie.seuilAlerteTauxRotationPct = this.seuilAlerteTauxRotationPct;
        copie.autoriserPeremptionEnSortie = this.autoriserPeremptionEnSortie;
        copie.exigerControleReception = this.exigerControleReception;
        copie.alertes = new ArrayList<>(this.alertes);
        return copie;
    }

    public StrategieRotationLot getStrategieRotationLot() { return strategieRotationLot; }
    public void setStrategieRotationLot(StrategieRotationLot strategieRotationLot) { this.strategieRotationLot = strategieRotationLot; }
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
    public List<AlerteStock> getAlertes() { return alertes; }
    public void setAlertes(List<AlerteStock> alertes) { this.alertes = alertes == null ? new ArrayList<>() : alertes; }
}