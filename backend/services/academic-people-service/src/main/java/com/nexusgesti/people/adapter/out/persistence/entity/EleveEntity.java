package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "eleve", schema = "academic_people")
public class EleveEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String matricule;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(name = "post_nom", length = 120)
    private String postNom;

    @Column(nullable = false, length = 120)
    private String prenom;

    @Column(nullable = false, length = 20)
    private String sexe;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 120)
    private String lieuNaissance;

    @Column(length = 60)
    private String nationalite;

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

    @Column(length = 500)
    private String photo;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "etablissement_origine", length = 160)
    private String etablissementOrigine;

    @Column(name = "etablissement_destination", length = 160)
    private String etablissementDestination;

    @Column(name = "motif_transfert", length = 255)
    private String motifTransfert;

    @Column(name = "statut_dossier", nullable = false, length = 20)
    private String statutDossier;

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
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
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
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }
    public String getEtablissementOrigine() { return etablissementOrigine; }
    public void setEtablissementOrigine(String etablissementOrigine) { this.etablissementOrigine = etablissementOrigine; }
    public String getEtablissementDestination() { return etablissementDestination; }
    public void setEtablissementDestination(String etablissementDestination) { this.etablissementDestination = etablissementDestination; }
    public String getMotifTransfert() { return motifTransfert; }
    public void setMotifTransfert(String motifTransfert) { this.motifTransfert = motifTransfert; }
    public String getStatutDossier() { return statutDossier; }
    public void setStatutDossier(String statutDossier) { this.statutDossier = statutDossier; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
