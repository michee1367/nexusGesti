package com.nexusgesti.identity.domain.model;

import java.util.UUID;

public class ProfilEtudiant {
    private UUID id;
    private String situation;
    private String profession;
    private String niveauInstruction;
    private String categorieSocioEconomique;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getNiveauInstruction() { return niveauInstruction; }
    public void setNiveauInstruction(String niveauInstruction) { this.niveauInstruction = niveauInstruction; }
    public String getCategorieSocioEconomique() { return categorieSocioEconomique; }
    public void setCategorieSocioEconomique(String categorieSocioEconomique) { this.categorieSocioEconomique = categorieSocioEconomique; }
}
