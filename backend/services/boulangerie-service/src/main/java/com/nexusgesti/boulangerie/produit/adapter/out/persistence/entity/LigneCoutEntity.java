package com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_cout", schema = "boulangerie")
public class LigneCoutEntity {

    @Id
    private UUID id;

    @Column(name = "recette_id", nullable = false)
    private UUID recetteId;

    @Column(nullable = false)
    private String nature;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRecetteId() { return recetteId; }
    public void setRecetteId(UUID recetteId) { this.recetteId = recetteId; }
    public String getNature() { return nature; }
    public void setNature(String nature) { this.nature = nature; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
