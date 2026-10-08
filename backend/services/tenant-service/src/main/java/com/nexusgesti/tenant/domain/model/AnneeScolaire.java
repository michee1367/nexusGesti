package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AnneeScolaire {

    private UUID idAnnee;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private TypeAnnee type;
    private Date dateDebut;
    private Date dateFin;
    private StatutAnnee statut;
    private int anneeReference;
    private UUID siteParDefautId;
    private List<Periode> periodes = new ArrayList<>();

    public AnneeScolaire ouvrir() {
        if (statut != StatutAnnee.PREPARATION && statut != StatutAnnee.CLOTUREE) {
            throw TenantException.transitionInvalide(statut.name(), StatutAnnee.EN_COURS.name());
        }
        this.statut = StatutAnnee.EN_COURS;
        return this;
    }

    public AnneeScolaire cloturer(Date date) {
        if (statut != StatutAnnee.EN_COURS) {
            throw TenantException.transitionInvalide(statut.name(), StatutAnnee.CLOTUREE.name());
        }
        boolean toutCloture = !periodes.isEmpty()
                && periodes.stream().noneMatch(p -> !p.isCloturee());
        if (!toutCloture) {
            throw TenantException.conflit("PERIODES_OUVERTES",
                    "Cloturer les periodes avant de cloturer l annee");
        }
        this.statut = StatutAnnee.CLOTUREE;
        return this;
    }

    public boolean estCourante() {
        return statut == StatutAnnee.EN_COURS;
    }

    public boolean contient(Date date) {
        if (date == null) {
            return false;
        }
        return !date.before(dateDebut) && !date.after(dateFin);
    }

    public UUID getIdAnnee() { return idAnnee; }
    public void setIdAnnee(UUID idAnnee) { this.idAnnee = idAnnee; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeAnnee getType() { return type; }
    public void setType(TypeAnnee type) { this.type = type; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public StatutAnnee getStatut() { return statut; }
    public void setStatut(StatutAnnee statut) { this.statut = statut; }
    public int getAnneeReference() { return anneeReference; }
    public void setAnneeReference(int anneeReference) { this.anneeReference = anneeReference; }
    public UUID getSiteParDefautId() { return siteParDefautId; }
    public void setSiteParDefautId(UUID siteParDefautId) { this.siteParDefautId = siteParDefautId; }
    public List<Periode> getPeriodes() { return periodes; }
    public void setPeriodes(List<Periode> periodes) { this.periodes = periodes == null ? new ArrayList<>() : periodes; }
}