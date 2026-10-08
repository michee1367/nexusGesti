package com.nexusgesti.tenant.domain.model;

import java.util.UUID;

public class Parametre {

    private UUID id;
    private UUID etablissementId;
    private String cle;
    private String valeur;
    private TypeValeur typeValeur;
    private String categorie;
    private String description;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public TypeValeur getTypeValeur() { return typeValeur; }
    public void setTypeValeur(TypeValeur typeValeur) { this.typeValeur = typeValeur; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}