package com.nexusgesti.tenant.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Filiere {

    private UUID idFiliere;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private String departement;
    private String diplome;
    private String niveauDiplome;
    private Integer dureeAnnees;
    private UUID cycleId;
    private boolean active;
    private List<Programme> programmes = new ArrayList<>();

    public UUID getIdFiliere() { return idFiliere; }
    public void setIdFiliere(UUID idFiliere) { this.idFiliere = idFiliere; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    public String getNiveauDiplome() { return niveauDiplome; }
    public void setNiveauDiplome(String niveauDiplome) { this.niveauDiplome = niveauDiplome; }
    public Integer getDureeAnnees() { return dureeAnnees; }
    public void setDureeAnnees(Integer dureeAnnees) { this.dureeAnnees = dureeAnnees; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Programme> getProgrammes() { return programmes; }
    public void setProgrammes(List<Programme> programmes) { this.programmes = programmes == null ? new ArrayList<>() : programmes; }
}