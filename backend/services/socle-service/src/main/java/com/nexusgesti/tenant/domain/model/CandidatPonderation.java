package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class CandidatPonderation {

    private ProfilEtablissement profil;
    private String cycle;
    private String niveau;
    private String parcours;
    private String programme;
    private String semestre;
    private String ue;
    private String cours;
    private String matiere;
    private UUID enseignant;
    private UUID classe;
    private String typeEvaluation;
    private String typeSession;
    private String typeSourceNote;

    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
    public String getUe() { return ue; }
    public void setUe(String ue) { this.ue = ue; }
    public String getCours() { return cours; }
    public void setCours(String cours) { this.cours = cours; }
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    public UUID getEnseignant() { return enseignant; }
    public void setEnseignant(UUID enseignant) { this.enseignant = enseignant; }
    public UUID getClasse() { return classe; }
    public void setClasse(UUID classe) { this.classe = classe; }
    public String getTypeEvaluation() { return typeEvaluation; }
    public void setTypeEvaluation(String typeEvaluation) { this.typeEvaluation = typeEvaluation; }
    public String getTypeSession() { return typeSession; }
    public void setTypeSession(String typeSession) { this.typeSession = typeSession; }
    public String getTypeSourceNote() { return typeSourceNote; }
    public void setTypeSourceNote(String typeSourceNote) { this.typeSourceNote = typeSourceNote; }
}