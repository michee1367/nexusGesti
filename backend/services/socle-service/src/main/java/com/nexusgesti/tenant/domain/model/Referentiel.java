package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Referentiel {

    private UUID idReferentiel;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private NatureReferentiel nature;
    private int ordre;
    private boolean actif;
    private List<ReferentielItem> items = new ArrayList<>();

    public Referentiel ajouterItem(String libelle, String code) {
        if (items.stream().anyMatch(i -> i.getCode().equals(code))) {
            throw TenantException.conflit("ITEM_EXISTANT", "Item deja present : " + code);
        }
        ReferentielItem item = new ReferentielItem();
        item.setIdItem(UUID.randomUUID());
        item.setReferentielId(this.idReferentiel);
        item.setLibelle(libelle);
        item.setCode(code);
        item.setOrdre(items.size());
        item.setActif(true);
        items.add(item);
        return this;
    }

    public Referentiel desactiver(String code) {
        items.stream()
                .filter(i -> i.getCode().equals(code))
                .forEach(i -> i.setActif(false));
        return this;
    }

    public UUID getIdReferentiel() { return idReferentiel; }
    public void setIdReferentiel(UUID idReferentiel) { this.idReferentiel = idReferentiel; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public NatureReferentiel getNature() { return nature; }
    public void setNature(NatureReferentiel nature) { this.nature = nature; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public List<ReferentielItem> getItems() { return items; }
    public void setItems(List<ReferentielItem> items) { this.items = items == null ? new ArrayList<>() : items; }
}