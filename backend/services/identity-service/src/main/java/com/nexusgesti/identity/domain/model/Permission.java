package com.nexusgesti.identity.domain.model;

import java.util.UUID;

public class Permission {
    private UUID idPermission;
    private String code;
    private String libelle;
    private String service;
    private String ressource;
    private String action;

    public boolean correspondA(String verbe, String ressource) {
        if (verbe == null || ressource == null) {
            return false;
        }
        return verbe.equalsIgnoreCase(this.action) && ressource.equalsIgnoreCase(this.ressource);
    }

    public UUID getIdPermission() { return idPermission; }
    public void setIdPermission(UUID idPermission) { this.idPermission = idPermission; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getRessource() { return ressource; }
    public void setRessource(String ressource) { this.ressource = ressource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
