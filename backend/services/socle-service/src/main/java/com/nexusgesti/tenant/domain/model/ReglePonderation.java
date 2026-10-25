package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class ReglePonderation {

    private UUID idRegle;
    private UUID grilleId;
    private String libelle;
    private TypePortee portee;
    private ProfilEtablissement profil;
    private UUID cycleId;
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
    private java.math.BigDecimal coefficient;
    private java.math.BigDecimal seuilInclusion;
    private java.math.BigDecimal plafondContribution;
    private java.math.BigDecimal poidsMax;
    private int priorite;
    private boolean active;

    public boolean sApplique(CandidatPonderation candidat) {
        if (!active || candidat == null) {
            return false;
        }
        return egaleOuSans(profil, candidat.getProfil())
                && egaleOuSans(niveau, candidat.getNiveau())
                && egaleOuSans(parcours, candidat.getParcours())
                && egaleOuSans(programme, candidat.getProgramme())
                && egaleOuSans(semestre, candidat.getSemestre())
                && egaleOuSans(ue, candidat.getUe())
                && egaleOuSans(cours, candidat.getCours())
                && egaleOuSans(matiere, candidat.getMatiere())
                && egaleOuSans(enseignant, candidat.getEnseignant())
                && egaleOuSans(classe, candidat.getClasse())
                && egaleOuSans(typeEvaluation, candidat.getTypeEvaluation())
                && egaleOuSans(typeSession, candidat.getTypeSession());
    }

    private <T> boolean egaleOuSans(T regle, T candidat) {
        if (regle == null) {
            return true;
        }
        return candidat != null && regle.equals(candidat);
    }

    public int nbCriteresRenseignes() {
        int n = 0;
        if (profil != null) n++;
        if (cycleId != null) n++;
        if (niveau != null) n++;
        if (parcours != null) n++;
        if (programme != null) n++;
        if (semestre != null) n++;
        if (ue != null) n++;
        if (cours != null) n++;
        if (matiere != null) n++;
        if (enseignant != null) n++;
        if (classe != null) n++;
        if (typeEvaluation != null) n++;
        if (typeSession != null) n++;
        return n;
    }

    public boolean estPlusSpecifique(ReglePonderation autre) {
        return nbCriteresRenseignes() > autre.nbCriteresRenseignes();
    }

    public UUID getIdRegle() { return idRegle; }
    public void setIdRegle(UUID idRegle) { this.idRegle = idRegle; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypePortee getPortee() { return portee; }
    public void setPortee(TypePortee portee) { this.portee = portee; }
    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
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
    public java.math.BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(java.math.BigDecimal coefficient) { this.coefficient = coefficient; }
    public java.math.BigDecimal getSeuilInclusion() { return seuilInclusion; }
    public void setSeuilInclusion(java.math.BigDecimal seuilInclusion) { this.seuilInclusion = seuilInclusion; }
    public java.math.BigDecimal getPlafondContribution() { return plafondContribution; }
    public void setPlafondContribution(java.math.BigDecimal plafondContribution) { this.plafondContribution = plafondContribution; }
    public java.math.BigDecimal getPoidsMax() { return poidsMax; }
    public void setPoidsMax(java.math.BigDecimal poidsMax) { this.poidsMax = poidsMax; }
    public int getPriorite() { return priorite; }
    public void setPriorite(int priorite) { this.priorite = priorite; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}