package com.nexusgesti.boulangerie.produit.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class LigneCout {

    private UUID id;
    private UUID recetteId;
    private NatureCout nature;
    private String libelle;
    private BigDecimal montant = BigDecimal.ZERO;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRecetteId() { return recetteId; }
    public void setRecetteId(UUID recetteId) { this.recetteId = recetteId; }
    public NatureCout getNature() { return nature; }
    public void setNature(NatureCout nature) { this.nature = nature; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
}
