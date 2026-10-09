package com.nexusgesti.identity.domain.model;

import java.util.UUID;

public class Adresse {
    private UUID id;
    private String rue;
    private String quartier;
    private String commune;
    private String ville;
    private String province;
    private String pays;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
