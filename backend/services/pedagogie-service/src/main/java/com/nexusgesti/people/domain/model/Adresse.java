package com.nexusgesti.people.domain.model;

public class Adresse {

    private String rue;
    private String quartier;
    private String commune;
    private String ville;
    private String province;
    private String pays;
    private String telephone;

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
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
