package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "parent", schema = "academic_people")
public class ParentEntity {

    @Id
    private UUID id;

    @Column(length = 30)
    private String matricule;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(name = "post_nom", length = 120)
    private String postNom;

    @Column(nullable = false, length = 120)
    private String prenom;

    @Column(nullable = false, length = 20)
    private String sexe;

    @Column(nullable = false, length = 40)
    private String telephone;

    @Column(name = "telephone_secondaire", length = 40)
    private String telephoneSecondaire;

    @Column(length = 160)
    private String email;

    @Column(name = "adresse_rue", length = 160)
    private String adresseRue;

    @Column(name = "adresse_quartier", length = 120)
    private String adresseQuartier;

    @Column(name = "adresse_commune", length = 120)
    private String adresseCommune;

    @Column(name = "adresse_ville", length = 120)
    private String adresseVille;

    @Column(name = "adresse_province", length = 120)
    private String adresseProvince;

    @Column(name = "adresse_pays", length = 120)
    private String adressePays;

    @Column(name = "adresse_telephone", length = 40)
    private String adresseTelephone;

    @Column(length = 120)
    private String profession;

    @Column(length = 40)
    private String statut;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getTelephoneSecondaire() { return telephoneSecondaire; }
    public void setTelephoneSecondaire(String telephoneSecondaire) { this.telephoneSecondaire = telephoneSecondaire; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresseRue() { return adresseRue; }
    public void setAdresseRue(String adresseRue) { this.adresseRue = adresseRue; }
    public String getAdresseQuartier() { return adresseQuartier; }
    public void setAdresseQuartier(String adresseQuartier) { this.adresseQuartier = adresseQuartier; }
    public String getAdresseCommune() { return adresseCommune; }
    public void setAdresseCommune(String adresseCommune) { this.adresseCommune = adresseCommune; }
    public String getAdresseVille() { return adresseVille; }
    public void setAdresseVille(String adresseVille) { this.adresseVille = adresseVille; }
    public String getAdresseProvince() { return adresseProvince; }
    public void setAdresseProvince(String adresseProvince) { this.adresseProvince = adresseProvince; }
    public String getAdressePays() { return adressePays; }
    public void setAdressePays(String adressePays) { this.adressePays = adressePays; }
    public String getAdresseTelephone() { return adresseTelephone; }
    public void setAdresseTelephone(String adresseTelephone) { this.adresseTelephone = adresseTelephone; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
