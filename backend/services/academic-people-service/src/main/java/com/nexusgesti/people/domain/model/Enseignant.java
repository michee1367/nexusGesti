package com.nexusgesti.people.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Enseignant {

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
    private String diplome;
    private String diplomePlus;
    private List<String> specialite = new ArrayList<>();
    private LocalDate dateRecrutement;
    private StatutEnseignant statut;
    private BigDecimal volumeHoraireHebdo;
    private UUID serviceRattachementId;
    private String photo;
    private List<PieceIdentiteEnseignant> pieces = new ArrayList<>();
    private List<AffectationEnseignant> affectations = new ArrayList<>();

    public BigDecimal getChargeHoraire(LocalDate date) {
        return affectations.stream()
                .filter(a -> a.estActive(date))
                .map(AffectationEnseignant::getChargeHoraireHebdo)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estTitulaireDe(UUID classeId) {
        return affectations.stream()
                .anyMatch(a -> a.isTitulaire() && classeId.equals(a.getClasseId()));
    }

    public List<UUID> getClasses() {
        return affectations.stream()
                .map(AffectationEnseignant::getClasseId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
    }

    public List<UUID> getMatieres() {
        return affectations.stream()
                .map(AffectationEnseignant::getMatiereId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
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
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    public String getDiplomePlus() { return diplomePlus; }
    public void setDiplomePlus(String diplomePlus) { this.diplomePlus = diplomePlus; }
    public List<String> getSpecialite() { return specialite; }
    public void setSpecialite(List<String> specialite) {
        this.specialite = specialite == null ? new ArrayList<>() : new ArrayList<>(specialite);
    }
    public LocalDate getDateRecrutement() { return dateRecrutement; }
    public void setDateRecrutement(LocalDate dateRecrutement) { this.dateRecrutement = dateRecrutement; }
    public StatutEnseignant getStatut() { return statut; }
    public void setStatut(StatutEnseignant statut) { this.statut = statut; }
    public BigDecimal getVolumeHoraireHebdo() { return volumeHoraireHebdo; }
    public void setVolumeHoraireHebdo(BigDecimal volumeHoraireHebdo) { this.volumeHoraireHebdo = volumeHoraireHebdo; }
    public UUID getServiceRattachementId() { return serviceRattachementId; }
    public void setServiceRattachementId(UUID serviceRattachementId) { this.serviceRattachementId = serviceRattachementId; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public List<PieceIdentiteEnseignant> getPieces() { return pieces; }
    public void setPieces(List<PieceIdentiteEnseignant> pieces) {
        this.pieces = pieces == null ? new ArrayList<>() : new ArrayList<>(pieces);
    }
    public List<AffectationEnseignant> getAffectations() { return affectations; }
    public void setAffectations(List<AffectationEnseignant> affectations) {
        this.affectations = affectations == null ? new ArrayList<>() : new ArrayList<>(affectations);
    }
}
