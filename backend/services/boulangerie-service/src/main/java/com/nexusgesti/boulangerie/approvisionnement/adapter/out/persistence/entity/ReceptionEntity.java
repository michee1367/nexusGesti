package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reception", schema = "boulangerie")
public class ReceptionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "commande_id", nullable = false)
    private UUID commandeId;

    @Column(name = "commande_numero", nullable = false)
    private String commandeNumero;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "recu_par")
    private UUID recuPar;

    @Column(name = "bon_livraison")
    private String bonLivraison;

    @Column(nullable = false)
    private String statut;

    @Column
    private String motif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public UUID getCommandeId() { return commandeId; }
    public void setCommandeId(UUID commandeId) { this.commandeId = commandeId; }
    public String getCommandeNumero() { return commandeNumero; }
    public void setCommandeNumero(String commandeNumero) { this.commandeNumero = commandeNumero; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public UUID getRecuPar() { return recuPar; }
    public void setRecuPar(UUID recuPar) { this.recuPar = recuPar; }
    public String getBonLivraison() { return bonLivraison; }
    public void setBonLivraison(String bonLivraison) { this.bonLivraison = bonLivraison; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
