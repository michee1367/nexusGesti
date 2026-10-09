package com.nexusgesti.identity.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Role {
    private UUID idRole;
    private String code;
    private String libelle;
    private CategorieRole categorie;
    private boolean systeme;
    private Integer hierarchique;
    private List<Permission> permissions = new ArrayList<>();

    public boolean estSysteme() {
        return systeme;
    }

    public UUID getIdRole() { return idRole; }
    public void setIdRole(UUID idRole) { this.idRole = idRole; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public CategorieRole getCategorie() { return categorie; }
    public void setCategorie(CategorieRole categorie) { this.categorie = categorie; }
    public boolean isSysteme() { return systeme; }
    public void setSysteme(boolean systeme) { this.systeme = systeme; }
    public Integer getHierarchique() { return hierarchique; }
    public void setHierarchique(Integer hierarchique) { this.hierarchique = hierarchique; }
    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
}
