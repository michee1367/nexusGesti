package com.nexusgesti.identity.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "utilisateur", schema = "socle")
public class UtilisateurEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "keycloak_user_id", nullable = false)
    private UUID keycloakUserId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 30)
    private String telephone;

    @Column(length = 100)
    private String nom;

    @Column(name = "post_nom", length = 100)
    private String postNom;

    @Column(length = 100)
    private String prenom;

    @Column(length = 10)
    private String sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 60)
    private String nationalite;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(name = "type_compte", nullable = false, length = 20)
    private String typeCompte;

    @Column(name = "langue_preferee", nullable = false, length = 10)
    private String languePreferee;

    @Column(name = "derniere_connexion")
    private Instant derniereConnexion;

    @Column(nullable = false)
    private boolean delombok;

    @Column(name = "adr_rue", length = 120)
    private String adrRue;

    @Column(name = "adr_quartier", length = 120)
    private String adrQuartier;

    @Column(name = "adr_commune", length = 120)
    private String adrCommune;

    @Column(name = "adr_ville", length = 120)
    private String adrVille;

    @Column(name = "adr_province", length = 120)
    private String adrProvince;

    @Column(name = "adr_pays", length = 60)
    private String adrPays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public UUID getKeycloakUserId() { return keycloakUserId; }
    public void setKeycloakUserId(UUID keycloakUserId) { this.keycloakUserId = keycloakUserId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
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
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    public String getLanguePreferee() { return languePreferee; }
    public void setLanguePreferee(String languePreferee) { this.languePreferee = languePreferee; }
    public Instant getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(Instant derniereConnexion) { this.derniereConnexion = derniereConnexion; }
    public boolean isDelombok() { return delombok; }
    public void setDelombok(boolean delombok) { this.delombok = delombok; }
    public String getAdrRue() { return adrRue; }
    public void setAdrRue(String adrRue) { this.adrRue = adrRue; }
    public String getAdrQuartier() { return adrQuartier; }
    public void setAdrQuartier(String adrQuartier) { this.adrQuartier = adrQuartier; }
    public String getAdrCommune() { return adrCommune; }
    public void setAdrCommune(String adrCommune) { this.adrCommune = adrCommune; }
    public String getAdrVille() { return adrVille; }
    public void setAdrVille(String adrVille) { this.adrVille = adrVille; }
    public String getAdrProvince() { return adrProvince; }
    public void setAdrProvince(String adrProvince) { this.adrProvince = adrProvince; }
    public String getAdrPays() { return adrPays; }
    public void setAdrPays(String adrPays) { this.adrPays = adrPays; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
