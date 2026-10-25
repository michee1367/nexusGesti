package com.nexusgesti.tenant.domain.model;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class GrilleConformite extends Grille {

    private Integer dureeRetentionDossierMedical;
    private Integer dureeConservationResultats;
    private Integer dureeConservationImages;
    private Integer dureeConservationEchantillons;
    private boolean chiffrementAuRepos;
    private boolean accesTracable;
    private Integer delaiDeclarationResultatCritique;
    private String niveauAccesResultatCritique;
    private boolean archivable;

    public int getDureeRetention(String objet) {
        Integer d = switch (objet) {
            case "DOSSIER_MEDICAL" -> dureeRetentionDossierMedical;
            case "RESULTATS" -> dureeConservationResultats;
            case "IMAGES" -> dureeConservationImages;
            case "ECHANTILLONS" -> dureeConservationEchantillons;
            default -> null;
        };
        return d == null ? 0 : d;
    }

    public boolean estExpire(String objet, Date date) {
        int duree = getDureeRetention(objet);
        if (duree <= 0 || date == null) {
            return false;
        }
        Instant limite = date.toInstant().plusSeconds(duree * 86400L);
        return Instant.now().isAfter(limite);
    }

    public boolean estArchivable(String objet, Date date) {
        return archivable && estExpire(objet, date);
    }

    public boolean exigeAccesTrace() {
        return accesTracable;
    }

    public int getDelaiDeclarationCritique() {
        return delaiDeclarationResultatCritique == null ? 0 : delaiDeclarationResultatCritique;
    }

    public GrilleConformite nouvelleVersion() {
        GrilleConformite copie = new GrilleConformite();
        copierCommun(copie);
        copie.dureeRetentionDossierMedical = this.dureeRetentionDossierMedical;
        copie.dureeConservationResultats = this.dureeConservationResultats;
        copie.dureeConservationImages = this.dureeConservationImages;
        copie.dureeConservationEchantillons = this.dureeConservationEchantillons;
        copie.chiffrementAuRepos = this.chiffrementAuRepos;
        copie.accesTracable = this.accesTracable;
        copie.delaiDeclarationResultatCritique = this.delaiDeclarationResultatCritique;
        copie.niveauAccesResultatCritique = this.niveauAccesResultatCritique;
        copie.archivable = this.archivable;
        return copie;
    }

    public Integer getDureeRetentionDossierMedical() { return dureeRetentionDossierMedical; }
    public void setDureeRetentionDossierMedical(Integer dureeRetentionDossierMedical) { this.dureeRetentionDossierMedical = dureeRetentionDossierMedical; }
    public Integer getDureeConservationResultats() { return dureeConservationResultats; }
    public void setDureeConservationResultats(Integer dureeConservationResultats) { this.dureeConservationResultats = dureeConservationResultats; }
    public Integer getDureeConservationImages() { return dureeConservationImages; }
    public void setDureeConservationImages(Integer dureeConservationImages) { this.dureeConservationImages = dureeConservationImages; }
    public Integer getDureeConservationEchantillons() { return dureeConservationEchantillons; }
    public void setDureeConservationEchantillons(Integer dureeConservationEchantillons) { this.dureeConservationEchantillons = dureeConservationEchantillons; }
    public boolean isChiffrementAuRepos() { return chiffrementAuRepos; }
    public void setChiffrementAuRepos(boolean chiffrementAuRepos) { this.chiffrementAuRepos = chiffrementAuRepos; }
    public boolean isAccesTracable() { return accesTracable; }
    public void setAccesTracable(boolean accesTracable) { this.accesTracable = accesTracable; }
    public Integer getDelaiDeclarationResultatCritique() { return delaiDeclarationResultatCritique; }
    public void setDelaiDeclarationResultatCritique(Integer delaiDeclarationResultatCritique) { this.delaiDeclarationResultatCritique = delaiDeclarationResultatCritique; }
    public String getNiveauAccesResultatCritique() { return niveauAccesResultatCritique; }
    public void setNiveauAccesResultatCritique(String niveauAccesResultatCritique) { this.niveauAccesResultatCritique = niveauAccesResultatCritique; }
    public boolean isArchivable() { return archivable; }
    public void setArchivable(boolean archivable) { this.archivable = archivable; }
}