package com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vente", schema = "boulangerie")
public class VenteEntity {

    @Id
    private UUID id;

    @Column(name = "numero_ticket", nullable = false)
    private String numeroTicket;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private String canal;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", nullable = false)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", nullable = false)
    private BigDecimal montantTTC;

    @Column(name = "mode_paiement", nullable = false)
    private String modePaiement;

    @Column(name = "points_fidelite_gagnes", nullable = false)
    private Integer pointsFideliteGagnes;

    @Column(name = "cle_idempotence", nullable = false)
    private String cleIdempotence;

    @Column(nullable = false)
    private String statut;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public Integer getPointsFideliteGagnes() { return pointsFideliteGagnes; }
    public void setPointsFideliteGagnes(Integer pointsFideliteGagnes) { this.pointsFideliteGagnes = pointsFideliteGagnes; }
    public String getCleIdempotence() { return cleIdempotence; }
    public void setCleIdempotence(String cleIdempotence) { this.cleIdempotence = cleIdempotence; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
