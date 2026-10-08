package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class Site {

    private UUID idSite;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private Adresse adresse;
    private boolean principal;

    public boolean estPrincipal() {
        return principal;
    }

    public UUID getIdSite() { return idSite; }
    public void setIdSite(UUID idSite) { this.idSite = idSite; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}