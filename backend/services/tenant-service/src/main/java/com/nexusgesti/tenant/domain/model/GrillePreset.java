package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class GrillePreset {

    private UUID idGrillePreset;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private ProfilEtablissement profil;
    private String description;
    private boolean systeme;

    public GrillePreset dupliquer(String libelle) {
        GrillePreset copie = new GrillePreset();
        copie.setCode(this.code);
        copie.setLibelle(libelle);
        copie.setProfil(this.profil);
        copie.setDescription(this.description);
        copie.setSysteme(false);
        return copie;
    }

    public UUID getIdGrillePreset() { return idGrillePreset; }
    public void setIdGrillePreset(UUID idGrillePreset) { this.idGrillePreset = idGrillePreset; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSysteme() { return systeme; }
    public void setSysteme(boolean systeme) { this.systeme = systeme; }
}