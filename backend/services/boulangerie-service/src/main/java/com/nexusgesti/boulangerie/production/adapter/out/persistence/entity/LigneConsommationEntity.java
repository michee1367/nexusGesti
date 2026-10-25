package com.nexusgesti.boulangerie.production.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_consommation", schema = "boulangerie")
public class LigneConsommationEntity {

    @Id
    private UUID id;

    @Column(name = "fournee_id", nullable = false)
    private UUID fourneeId;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(name = "lot_numero")
    private String lotNumero;

    @Column(name = "quantite_theorique", nullable = false)
    private BigDecimal quantiteTheorique;

    @Column(name = "quantite_reelle", nullable = false)
    private BigDecimal quantiteReelle;

    @Column(name = "prix_moyen", nullable = false)
    private BigDecimal prixMoyen;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFourneeId() { return fourneeId; }
    public void setFourneeId(UUID fourneeId) { this.fourneeId = fourneeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getLotNumero() { return lotNumero; }
    public void setLotNumero(String lotNumero) { this.lotNumero = lotNumero; }
    public BigDecimal getQuantiteTheorique() { return quantiteTheorique; }
    public void setQuantiteTheorique(BigDecimal quantiteTheorique) { this.quantiteTheorique = quantiteTheorique; }
    public BigDecimal getQuantiteReelle() { return quantiteReelle; }
    public void setQuantiteReelle(BigDecimal quantiteReelle) { this.quantiteReelle = quantiteReelle; }
    public BigDecimal getPrixMoyen() { return prixMoyen; }
    public void setPrixMoyen(BigDecimal prixMoyen) { this.prixMoyen = prixMoyen; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
