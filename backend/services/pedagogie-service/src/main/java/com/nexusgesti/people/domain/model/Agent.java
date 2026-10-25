package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Agent {

    private UUID id;
    private String matricule;
    private String nom;
    private String postNom;
    private String prenom;
    private Sexe sexe;
    private LocalDate dateNaissance;
    private String telephone;
    private String email;
    private Adresse adresse;
    private String fonctionOfficielle;
    private LocalDate dateRecrutement;
    private String statut;
    private UUID serviceRattachementId;

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
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public String getFonctionOfficielle() { return fonctionOfficielle; }
    public void setFonctionOfficielle(String fonctionOfficielle) { this.fonctionOfficielle = fonctionOfficielle; }
    public LocalDate getDateRecrutement() { return dateRecrutement; }
    public void setDateRecrutement(LocalDate dateRecrutement) { this.dateRecrutement = dateRecrutement; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getServiceRattachementId() { return serviceRattachementId; }
    public void setServiceRattachementId(UUID serviceRattachementId) { this.serviceRattachementId = serviceRattachementId; }
}
