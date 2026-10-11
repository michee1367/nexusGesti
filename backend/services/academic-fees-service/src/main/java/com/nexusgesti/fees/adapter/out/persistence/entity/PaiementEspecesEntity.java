package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "paiement_especes", schema = "academic_fees")
public class PaiementEspecesEntity {

    @Id
    private UUID id;

    @Column(name = "paiement_id", nullable = false)
    private UUID paiementId;

    private UUID caja;

    @Column(name = "numero_recepisse")
    private String numeroRecepisse;

    private UUID agent;

    @Column(nullable = false)
    private boolean contresigne;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public UUID getCaja() { return caja; }
    public void setCaja(UUID caja) { this.caja = caja; }
    public String getNumeroRecepisse() { return numeroRecepisse; }
    public void setNumeroRecepisse(String numeroRecepisse) { this.numeroRecepisse = numeroRecepisse; }
    public UUID getAgent() { return agent; }
    public void setAgent(UUID agent) { this.agent = agent; }
    public boolean isContresigne() { return contresigne; }
    public void setContresigne(boolean contresigne) { this.contresigne = contresigne; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}