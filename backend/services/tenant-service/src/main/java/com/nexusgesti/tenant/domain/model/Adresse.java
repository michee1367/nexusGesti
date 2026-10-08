package com.nexusgesti.tenant.domain.model;

public class Adresse {

    private String rue;
    private String avenue;
    private String quartier;
    private String commune;
    private String ville;
    private String province;
    private String territoire;
    private String codePostal;
    private String pays;

    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    public String getAvenue() { return avenue; }
    public void setAvenue(String avenue) { this.avenue = avenue; }
    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getTerritoire() { return territoire; }
    public void setTerritoire(String territoire) { this.territoire = territoire; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}