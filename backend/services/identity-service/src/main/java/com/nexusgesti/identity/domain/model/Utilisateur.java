package com.nexusgesti.identity.domain.model;

import com.nexusgesti.identity.domain.IdentityException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utilisateur {

    private UUID idUtilisateur;
    private String identifiant;
    private String email;
    private String telephone;
    private String nom;
    private String postNom;
    private String prenom;
    private String sexe;
    private LocalDate dateNaissance;
    private String nationalite;
    private String photoUrl;
    private StatutUtilisateur statut;
    private TypeCompte typeCompte;
    private boolean doitChangerMotDePasse;
    private Instant derniereConnexion;
    private boolean delombok;
    private String languePreferee;

    private UUID keycloakUserId;
    private Adresse adresse;
    private ProfilEtudiant profilEtudiant;
    private final List<IdentifiantExterne> identifiantsExternes = new ArrayList<>();
    private final List<MotifDesactivation> motifsDesactivation = new ArrayList<>();
    private final List<Preference> preferences = new ArrayList<>();
    private final List<PersonneLien> personnesLien = new ArrayList<>();
    private final List<Role> roles = new ArrayList<>();
    private final List<TypePersonne> typesPersonne = new ArrayList<>();
    private LocalDate suspenduJusquau;

    public Utilisateur activer() {
        if (statut != StatutUtilisateur.INVITE
                && statut != StatutUtilisateur.DESACTIF
                && statut != StatutUtilisateur.SUSPENDU) {
            throw IdentityException.transitionInvalide(statut == null ? "null" : statut.name(), StatutUtilisateur.ACTIF.name());
        }
        this.statut = StatutUtilisateur.ACTIF;
        this.suspenduJusquau = null;
        return this;
    }

    public Utilisateur desactiver(String motif) {
        if (motif == null || motif.isBlank()) {
            throw IdentityException.conflit("MOTIF_REQUIS", "Le motif de desactivation est obligatoire");
        }
        this.motifsDesactivation.add(new MotifDesactivation(motif, null));
        this.statut = StatutUtilisateur.DESACTIF;
        return this;
    }

    public Utilisateur suspendre(LocalDate jusquau) {
        if (statut != StatutUtilisateur.ACTIF) {
            throw IdentityException.transitionInvalide(statut == null ? "null" : statut.name(), StatutUtilisateur.SUSPENDU.name());
        }
        this.suspenduJusquau = jusquau;
        this.statut = StatutUtilisateur.SUSPENDU;
        return this;
    }

    public Utilisateur attribuerRole(Role role) {
        if (role == null || role.getCode() == null) {
            throw IdentityException.conflit("ROLE_INVALIDE", "Role invalide");
        }
        if (!aRole(role.getCode())) {
            this.roles.add(role);
        }
        return this;
    }

    public Utilisateur retirerRole(String nomRole) {
        this.roles.removeIf(r -> r.getCode().equalsIgnoreCase(nomRole));
        return this;
    }

    public boolean aRole(String nomRole) {
        return roles.stream().anyMatch(r -> r.getCode().equalsIgnoreCase(nomRole));
    }

    public List<Role> getRolesActifs() {
        return roles;
    }

    public UUID getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(UUID idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
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
    public StatutUtilisateur getStatut() { return statut; }
    public void setStatut(StatutUtilisateur statut) { this.statut = statut; }
    public TypeCompte getTypeCompte() { return typeCompte; }
    public void setTypeCompte(TypeCompte typeCompte) { this.typeCompte = typeCompte; }
    public boolean isDoitChangerMotDePasse() { return doitChangerMotDePasse; }
    public void setDoitChangerMotDePasse(boolean doitChangerMotDePasse) { this.doitChangerMotDePasse = doitChangerMotDePasse; }
    public Instant getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(Instant derniereConnexion) { this.derniereConnexion = derniereConnexion; }
    public boolean isDelombok() { return delombok; }
    public void setDelombok(boolean delombok) { this.delombok = delombok; }
    public String getLanguePreferee() { return languePreferee; }
    public void setLanguePreferee(String languePreferee) { this.languePreferee = languePreferee; }
    public UUID getKeycloakUserId() { return keycloakUserId; }
    public void setKeycloakUserId(UUID keycloakUserId) { this.keycloakUserId = keycloakUserId; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public ProfilEtudiant getProfilEtudiant() { return profilEtudiant; }
    public void setProfilEtudiant(ProfilEtudiant profilEtudiant) { this.profilEtudiant = profilEtudiant; }
    public List<IdentifiantExterne> getIdentifiantsExternes() { return identifiantsExternes; }
    public List<MotifDesactivation> getMotifsDesactivation() { return motifsDesactivation; }
    public List<Preference> getPreferences() { return preferences; }
    public List<PersonneLien> getPersonnesLien() { return personnesLien; }
    public List<Role> getRoles() { return roles; }
    public List<TypePersonne> getTypesPersonne() { return typesPersonne; }
    public LocalDate getSuspenduJusquau() { return suspenduJusquau; }
    public void setSuspenduJusquau(LocalDate suspenduJusquau) { this.suspenduJusquau = suspenduJusquau; }
}
