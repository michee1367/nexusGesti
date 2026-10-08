package com.nexusgesti.tenant.domain.model;

import java.util.Map;
import java.util.UUID;

public class ReferentielItem {

    private UUID idItem;
    private UUID referentielId;
    private String code;
    private String libelle;
    private String valeur;
    private Map<String, String> metaDonnees;
    private int ordre;
    private boolean actif;
    private Boolean comptabilisePresence;
    private Boolean bloqueSaisie;

    public boolean estCocheable() {
        return bloqueSaisie == null || !bloqueSaisie;
    }

    public UUID getIdItem() { return idItem; }
    public void setIdItem(UUID idItem) { this.idItem = idItem; }
    public UUID getReferentielId() { return referentielId; }
    public void setReferentielId(UUID referentielId) { this.referentielId = referentielId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public Map<String, String> getMetaDonnees() { return metaDonnees; }
    public void setMetaDonnees(Map<String, String> metaDonnees) { this.metaDonnees = metaDonnees; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Boolean getComptabilisePresence() { return comptabilisePresence; }
    public void setComptabilisePresence(Boolean comptabilisePresence) { this.comptabilisePresence = comptabilisePresence; }
    public Boolean getBloqueSaisie() { return bloqueSaisie; }
    public void setBloqueSaisie(Boolean bloqueSaisie) { this.bloqueSaisie = bloqueSaisie; }
}