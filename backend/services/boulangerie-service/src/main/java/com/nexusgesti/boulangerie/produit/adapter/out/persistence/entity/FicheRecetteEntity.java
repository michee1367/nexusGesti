package com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fiche_recette", schema = "boulangerie")
public class FicheRecetteEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(name = "produit_code", nullable = false)
    private String produitCode;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "rendement_quantite", nullable = false)
    private BigDecimal rendementQuantite;

    @Column(name = "rendement_unite", nullable = false)
    private String rendementUnite;

    @Column(name = "temps_preparation_min", nullable = false)
    private Integer tempsPreparationMin;

    @Column(name = "temps_cuisson_min", nullable = false)
    private Integer tempsCuissonMin;

    @Column(name = "temperature_cuisson_c")
    private Integer temperatureCuissonC;

    private String instructions;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean modifiable;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public BigDecimal getRendementQuantite() { return rendementQuantite; }
    public void setRendementQuantite(BigDecimal rendementQuantite) { this.rendementQuantite = rendementQuantite; }
    public String getRendementUnite() { return rendementUnite; }
    public void setRendementUnite(String rendementUnite) { this.rendementUnite = rendementUnite; }
    public Integer getTempsPreparationMin() { return tempsPreparationMin; }
    public void setTempsPreparationMin(Integer tempsPreparationMin) { this.tempsPreparationMin = tempsPreparationMin; }
    public Integer getTempsCuissonMin() { return tempsCuissonMin; }
    public void setTempsCuissonMin(Integer tempsCuissonMin) { this.tempsCuissonMin = tempsCuissonMin; }
    public Integer getTemperatureCuissonC() { return temperatureCuissonC; }
    public void setTemperatureCuissonC(Integer temperatureCuissonC) { this.temperatureCuissonC = temperatureCuissonC; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
