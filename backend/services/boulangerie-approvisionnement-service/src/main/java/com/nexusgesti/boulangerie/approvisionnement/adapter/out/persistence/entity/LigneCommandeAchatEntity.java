package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_commande_achat", schema = "boulangerie_approvisionnement")
public class LigneCommandeAchatEntity {

    @Id
    private UUID id;

    @Column(name = "commande_id", nullable = false)
    private UUID commandeId;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(nullable = false)
    private String designation;

    @Column(name = "quantite_commandee", nullable = false)
    private BigDecimal quantiteCommandee;

    @Column(name = "quantite_recue", nullable = false)
    private BigDecimal quantiteRecue;

    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;

    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTva;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHT;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCommandeId() { return commandeId; }
    public void setCommandeId(UUID commandeId) { this.commandeId = commandeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(BigDecimal quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }
    public BigDecimal getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(BigDecimal quantiteRecue) { this.quantiteRecue = quantiteRecue; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
