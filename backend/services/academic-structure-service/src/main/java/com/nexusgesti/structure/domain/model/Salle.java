package com.nexusgesti.structure.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Salle {

    private UUID idSalle;
    private String code;
    private String libelle;
    private TypeSalle type;
    private UUID siteId;
    private int capacite;
    private List<String> equipements = new ArrayList<>();
    private boolean accessible;
    private boolean disponible = true;

    public boolean estCompatible(TypeSalle typeExige, int effectif) {
        if (!disponible) {
            return false;
        }
        if (capacite < effectif) {
            return false;
        }
        return typeExige == null || typeExige == this.type;
    }

    public UUID getIdSalle() { return idSalle; }
    public void setIdSalle(UUID idSalle) { this.idSalle = idSalle; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeSalle getType() { return type; }
    public void setType(TypeSalle type) { this.type = type; }
    public UUID getSiteId() { return siteId; }
    public void setSiteId(UUID siteId) { this.siteId = siteId; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public List<String> getEquipements() { return equipements; }
    public void setEquipements(List<String> equipements) { this.equipements = equipements == null ? new ArrayList<>() : new ArrayList<>(equipements); }
    public boolean isAccessible() { return accessible; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
