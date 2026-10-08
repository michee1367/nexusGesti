package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grille_conformite", schema = "tenant")
public class GrilleConformiteEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false, length = 20)
    private String profil;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "date_activation")
    private Instant dateActivation;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean modifiable;

    @Column(name = "duree_retention_dossier_medical")
    private Integer dureeRetentionDossierMedical;

    @Column(name = "duree_conservation_resultats")
    private Integer dureeConservationResultats;

    @Column(name = "duree_conservation_images")
    private Integer dureeConservationImages;

    @Column(name = "duree_conservation_echantillons")
    private Integer dureeConservationEchantillons;

    @Column(name = "chiffrement_au_repos", nullable = false)
    private boolean chiffrementAuRepos;

    @Column(name = "acces_tracable", nullable = false)
    private boolean accesTracable;

    @Column(name = "delai_declaration_resultat_critique")
    private Integer delaiDeclarationResultatCritique;

    @Column(name = "niveau_acces_resultat_critique", length = 60)
    private String niveauAccesResultatCritique;

    @Column(nullable = false)
    private boolean archivable;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}