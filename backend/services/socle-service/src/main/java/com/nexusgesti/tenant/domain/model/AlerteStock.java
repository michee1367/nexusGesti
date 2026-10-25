package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.util.List;
import java.util.UUID;

public class AlerteStock {

    private UUID idAlerte;
    private UUID grilleId;
    private String reference;
    private String depot;
    private java.math.BigDecimal seuil;
    private NiveauAlerte niveau;
    private List<UUID> destinataires;
    private boolean active;

    public AlerteStock declencher() {
        this.active = true;
        return this;
    }

    public AlerteStock acquitter(UUID utilisateur) {
        if (!active) {
            throw TenantException.transitionInvalide("ACQUITTEE", "ACQUITTER");
        }
        if (utilisateur != null && destinataires != null && !destinataires.contains(utilisateur)) {
            destinataires = new java.util.ArrayList<>(destinataires);
            destinataires.add(utilisateur);
        }
        this.active = false;
        return this;
    }

    public UUID getIdAlerte() { return idAlerte; }
    public void setIdAlerte(UUID idAlerte) { this.idAlerte = idAlerte; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDepot() { return depot; }
    public void setDepot(String depot) { this.depot = depot; }
    public java.math.BigDecimal getSeuil() { return seuil; }
    public void setSeuil(java.math.BigDecimal seuil) { this.seuil = seuil; }
    public NiveauAlerte getNiveau() { return niveau; }
    public void setNiveau(NiveauAlerte niveau) { this.niveau = niveau; }
    public List<UUID> getDestinataires() { return destinataires; }
    public void setDestinataires(List<UUID> destinataires) { this.destinataires = destinataires; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}