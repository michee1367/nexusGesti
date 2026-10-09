package com.nexusgesti.people.domain.model;

import java.util.UUID;

public class Parent {

    private UUID id;
    private String matricule;
    private String nom;
    private String postNom;
    private String prenom;
    private Sexe sexe;
    private String telephone;
    private String telephoneSecondaire;
    private String email;
    private Adresse adresse;
    private String profession;
    private String statut;

    public String getContactPrioritaire() {
        if (telephone != null && !telephone.isBlank()) {
            return telephone;
        }
        if (telephoneSecondaire != null && !telephoneSecondaire.isBlank()) {
            return telephoneSecondaire;
        }
        return email;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPostNom() { return postNom; }
    public void setPostNom(String postNom) { this.postNom = postNom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getTelephoneSecondaire() { return telephoneSecondaire; }
    public void setTelephoneSecondaire(String telephoneSecondaire) { this.telephoneSecondaire = telephoneSecondaire; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
