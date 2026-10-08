package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class Cycle {

    private UUID id;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private int rang;
    private ProfilEtablissement profil;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
}