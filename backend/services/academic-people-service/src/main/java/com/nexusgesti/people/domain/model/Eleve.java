package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Eleve {

    private UUID idEleve;
    private String matricule;
    private String nom;
    private String postNom;
    private String prenom;
    private Sexe sexe;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String nationalite;
    private Adresse adresse;
    private String photo;
    private StatutEleve statut;
    private LocalDate dateInscription;
    private LocalDate dateSortie;
    private String etablissementOrigine;
    private String etablissementDestination;
    private String motifTransfert;
    private StatutDossier statutDossier;
    private DossierEleve dossier;
    private List<Inscription> inscriptions = new ArrayList<>();
    private List<CongeEtude> conges = new ArrayList<>();
    private List<SanctionDisciplinaire> sanctions = new ArrayList<>();

    public boolean estInscrit(LocalDate date) {
        boolean inscrit = statut == StatutEleve.INSCRIT || statut == StatutEleve.REINSCRIT;
        boolean apresEntree = dateInscription == null || !date.isBefore(dateInscription);
        boolean avantSortie = dateSortie == null || !date.isAfter(dateSortie);
        return inscrit && apresEntree && avantSortie;
    }

    public Integer getAge(LocalDate date) {
        if (dateNaissance == null) {
            return null;
        }
        return Period.between(dateNaissance, date).getYears();
    }

    public Eleve transferer(String destination, String motif) {
        this.statut = StatutEleve.TRANSFERE;
        this.etablissementDestination = destination;
        this.motifTransfert = motif;
        this.dateSortie = LocalDate.now();
        return this;
    }

    public Eleve radier(String motif) {
        this.statut = StatutEleve.RADIE;
        this.motifTransfert = motif;
        this.dateSortie = LocalDate.now();
        return this;
    }

    public DossierEleve obtenirDossier() {
        if (dossier == null) {
            dossier = new DossierEleve();
            dossier.setId(UUID.randomUUID());
            dossier.setEleveId(idEleve);
            dossier.setReference("DOS-" + (matricule == null ? UUID.randomUUID() : matricule));
            dossier.setDateOuverture(dateInscription == null ? LocalDate.now() : dateInscription);
            dossier.setStatut(StatutDossier.EN_COURS);
        }
        return dossier;
    }

    public Inscription ajouterInscription(Inscription inscription) {
        if (inscription.getIdInscription() == null) {
            inscription.setIdInscription(UUID.randomUUID());
        }
        inscription.setEleveId(idEleve);
        inscriptions.add(inscription);
        return inscription;
    }

    public UUID getClasseCourante(LocalDate date) {
        return inscriptions.stream()
                .filter(Inscription::estEffectivementInscrit)
                .filter(i -> i.getDateInscription() == null || !date.isBefore(i.getDateInscription()))
                .reduce((a, b) -> b)
                .map(Inscription::getClasseId)
                .orElse(null);
    }

    public UUID getIdEleve() { return idEleve; }
    public void setIdEleve(UUID idEleve) { this.idEleve = idEleve; }
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
    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public StatutEleve getStatut() { return statut; }
    public void setStatut(StatutEleve statut) { this.statut = statut; }
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
    public StatutDossier getStatutDossier() { return statutDossier; }
    public void setStatutDossier(StatutDossier statutDossier) { this.statutDossier = statutDossier; }
    public DossierEleve getDossier() { return dossier; }
    public void setDossier(DossierEleve dossier) { this.dossier = dossier; }
    public List<Inscription> getInscriptions() { return inscriptions; }
    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions == null ? new ArrayList<>() : new ArrayList<>(inscriptions);
    }
    public List<CongeEtude> getConges() { return conges; }
    public void setConges(List<CongeEtude> conges) {
        this.conges = conges == null ? new ArrayList<>() : new ArrayList<>(conges);
    }
    public List<SanctionDisciplinaire> getSanctions() { return sanctions; }
    public void setSanctions(List<SanctionDisciplinaire> sanctions) {
        this.sanctions = sanctions == null ? new ArrayList<>() : new ArrayList<>(sanctions);
    }
}
