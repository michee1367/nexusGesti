package com.nexusgesti.structure.domain.model;

import java.util.ArrayList;
import java.util.List;

public class RegimeHoraire {

    private String code;
    private String libelle;
    private List<JourSemaine> jours = new ArrayList<>();
    private int creneauxParJour;
    private int dureeCreneau;
    private Integer pauseMatinale;
    private Integer pauseMeridienne;

    public int getVolumeHebdomadaire() {
        return jours.size() * creneauxParJour * dureeCreneau;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public List<JourSemaine> getJours() { return jours; }
    public void setJours(List<JourSemaine> jours) { this.jours = jours == null ? new ArrayList<>() : new ArrayList<>(jours); }
    public int getCreneauxParJour() { return creneauxParJour; }
    public void setCreneauxParJour(int creneauxParJour) { this.creneauxParJour = creneauxParJour; }
    public int getDureeCreneau() { return dureeCreneau; }
    public void setDureeCreneau(int dureeCreneau) { this.dureeCreneau = dureeCreneau; }
    public Integer getPauseMatinale() { return pauseMatinale; }
    public void setPauseMatinale(Integer pauseMatinale) { this.pauseMatinale = pauseMatinale; }
    public Integer getPauseMeridienne() { return pauseMeridienne; }
    public void setPauseMeridienne(Integer pauseMeridienne) { this.pauseMeridienne = pauseMeridienne; }
}
