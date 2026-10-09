package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class LienParentalite {

    private UUID id;
    private UUID eleveId;
    private UUID parentId;
    private TypeLien type;
    private boolean principal;
    private boolean payeur;
    private boolean receveurNotifications;
    private LocalDate dateDebut;

    public boolean estContactPrioritaire() {
        return principal;
    }

    public boolean estPayeur() {
        return payeur;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public TypeLien getType() { return type; }
    public void setType(TypeLien type) { this.type = type; }
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
    public boolean isPayeur() { return payeur; }
    public void setPayeur(boolean payeur) { this.payeur = payeur; }
    public boolean isReceveurNotifications() { return receveurNotifications; }
    public void setReceveurNotifications(boolean receveurNotifications) { this.receveurNotifications = receveurNotifications; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
}
