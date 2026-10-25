package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reduction", schema = "pedagogie")
public class ReductionEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false)
    private String code;

    private String libelle;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private BigDecimal valeur;

    @Column(nullable = false)
    private boolean cumulable;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public boolean isCumulable() { return cumulable; }
    public void setCumulable(boolean cumulable) { this.cumulable = cumulable; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}