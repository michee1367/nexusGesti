package com.nexusgesti.boulangerie.production.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fournee", schema = "boulangerie")
public class FourneeEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "recette_code", nullable = false)
    private String recetteCode;

    @Column(name = "version_recette")
    private Integer versionRecette;

    @Column(name = "produit_code")
    private String produitCode;

    @Column(name = "four_code")
    private String fourCode;

    @Column(name = "operateur_id")
    private UUID operateurId;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "quantite_prevue", nullable = false)
    private BigDecimal quantitePrevue;

    @Column(name = "quantite_realisee", nullable = false)
    private BigDecimal quantiteRealisee;

    @Column(nullable = false)
    private String statut;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getRecetteCode() { return recetteCode; }
    public void setRecetteCode(String recetteCode) { this.recetteCode = recetteCode; }
    public Integer getVersionRecette() { return versionRecette; }
    public void setVersionRecette(Integer versionRecette) { this.versionRecette = versionRecette; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getFourCode() { return fourCode; }
    public void setFourCode(String fourCode) { this.fourCode = fourCode; }
    public UUID getOperateurId() { return operateurId; }
    public void setOperateurId(UUID operateurId) { this.operateurId = operateurId; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public BigDecimal getQuantitePrevue() { return quantitePrevue; }
    public void setQuantitePrevue(BigDecimal quantitePrevue) { this.quantitePrevue = quantitePrevue; }
    public BigDecimal getQuantiteRealisee() { return quantiteRealisee; }
    public void setQuantiteRealisee(BigDecimal quantiteRealisee) { this.quantiteRealisee = quantiteRealisee; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
