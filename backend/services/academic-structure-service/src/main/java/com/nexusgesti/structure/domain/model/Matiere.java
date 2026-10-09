package com.nexusgesti.structure.domain.model;

import java.util.UUID;

public class Matiere {

    private UUID idMatiere;
    private String code;
    private String libelle;
    private String discipline;
    private TypeMatiere type;
    private String couleur;
    private boolean active = true;

    public UUID getIdMatiere() { return idMatiere; }
    public void setIdMatiere(UUID idMatiere) { this.idMatiere = idMatiere; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }
    public TypeMatiere getType() { return type; }
    public void setType(TypeMatiere type) { this.type = type; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
