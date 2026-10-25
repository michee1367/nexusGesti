package com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "commande_client", schema = "boulangerie_vente")
public class CommandeClientEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "client_code", nullable = false)
    private String clientCode;

    @Column(name = "date_commande", nullable = false)
    private LocalDate dateCommande;

    @Column(name = "date_retrait")
    private LocalDate dateRetrait;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHT;

    @Column(name = "montant_ttc", nullable = false)
    private BigDecimal montantTTC;

    @Column(nullable = false)
    private BigDecimal acompte;

    @Column(nullable = false)
    private String statut;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public LocalDate getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public LocalDate getDateRetrait() { return dateRetrait; }
    public void setDateRetrait(LocalDate dateRetrait) { this.dateRetrait = dateRetrait; }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }
    public BigDecimal getAcompte() { return acompte; }
    public void setAcompte(BigDecimal acompte) { this.acompte = acompte; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
