package com.nexusgesti.identity.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleTemplate {
    private UUID id;
    private String code;
    private String libelle;
    private String profil;
    private List<String> permissions = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
