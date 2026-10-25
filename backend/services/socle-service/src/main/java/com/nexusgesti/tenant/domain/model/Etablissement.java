package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.util.UUID;

public class Etablissement {

    private UUID id;
    private String code;
    private String denomination;
    private String sigle;
    private ProfilEtablissement profil;
    private StatutEtablissement statut;
    private Adresse adresse;
    private String telephone;
    private String emailOfficiel;
    private String devise;
    private String pays;
    private String fuseauHoraire;
    private Integer capaciteAccueil;

    public Etablissement activer() {
        if (statut != StatutEtablissement.PREPARATION && statut != StatutEtablissement.SUSPENDU) {
            throw TenantException.transitionInvalide(statut.name(), StatutEtablissement.ACTIF.name());
        }
        this.statut = StatutEtablissement.ACTIF;
        return this;
    }

    public Etablissement suspendre(String motif) {
        if (statut != StatutEtablissement.ACTIF) {
            throw TenantException.transitionInvalide(statut.name(), StatutEtablissement.SUSPENDU.name());
        }
        if (motif == null || motif.isBlank()) {
            throw TenantException.conflit("MOTIF_REQUIS", "Le motif de suspension est obligatoire");
        }
        this.statut = StatutEtablissement.SUSPENDU;
        return this;
    }

    public Etablissement archiver() {
        if (statut != StatutEtablissement.ACTIF && statut != StatutEtablissement.SUSPENDU) {
            throw TenantException.transitionInvalide(statut.name(), StatutEtablissement.ARCHIVE.name());
        }
        this.statut = StatutEtablissement.ARCHIVE;
        return this;
    }

    public boolean estOperatif() {
        return statut == StatutEtablissement.ACTIF;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDenomination() { return denomination; }
    public void setDenomination(String denomination) { this.denomination = denomination; }
    public String getSigle() { return sigle; }
    public void setSigle(String sigle) { this.sigle = sigle; }
    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
    public StatutEtablissement getStatut() { return statut; }
    public void setStatut(StatutEtablissement statut) { this.statut = statut; }
    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmailOfficiel() { return emailOfficiel; }
    public void setEmailOfficiel(String emailOfficiel) { this.emailOfficiel = emailOfficiel; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getFuseauHoraire() { return fuseauHoraire; }
    public void setFuseauHoraire(String fuseauHoraire) { this.fuseauHoraire = fuseauHoraire; }
    public Integer getCapaciteAccueil() { return capaciteAccueil; }
    public void setCapaciteAccueil(Integer capaciteAccueil) { this.capaciteAccueil = capaciteAccueil; }
}