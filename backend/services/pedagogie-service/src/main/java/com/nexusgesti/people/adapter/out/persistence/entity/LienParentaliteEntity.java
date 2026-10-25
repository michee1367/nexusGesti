package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lien_parentalite", schema = "pedagogie")
public class LienParentaliteEntity {

    @Id
    private UUID id;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false)
    private boolean principal;

    @Column(nullable = false)
    private boolean payeur;

    @Column(name = "receveur_notifications", nullable = false)
    private boolean receveurNotifications;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
    public boolean isPayeur() { return payeur; }
    public void setPayeur(boolean payeur) { this.payeur = payeur; }
    public boolean isReceveurNotifications() { return receveurNotifications; }
    public void setReceveurNotifications(boolean receveurNotifications) { this.receveurNotifications = receveurNotifications; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
