package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.time.Instant;
import java.util.UUID;

public abstract class Grille {

    private UUID id;
    private UUID etablissementId;
    private String code;
    private String libelle;
    private int version;
    private ProfilEtablissement profil;
    private UUID anneeId;
    private Instant dateActivation;
    private boolean active;
    private boolean modifiable = true;

    public Grille activer() {
        if (estFigee()) {
            throw TenantException.transitionInvalide("FIGEE", "ACTIVER");
        }
        this.active = true;
        this.dateActivation = Instant.now();
        return this;
    }

    public Grille figer() {
        if (estFigee()) {
            throw TenantException.transitionInvalide("FIGEE", "FIGER");
        }
        this.modifiable = false;
        return this;
    }

    public boolean estFigee() {
        return !modifiable;
    }

    protected void copierCommun(Grille target) {
        target.etablissementId = this.etablissementId;
        target.code = this.code;
        target.libelle = this.libelle;
        target.profil = this.profil;
        target.anneeId = this.anneeId;
        target.version = this.version + 1;
        target.active = false;
        target.modifiable = true;
        target.dateActivation = null;
    }

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
    public ProfilEtablissement getProfil() { return profil; }
    public void setProfil(ProfilEtablissement profil) { this.profil = profil; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
}