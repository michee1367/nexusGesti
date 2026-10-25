package com.nexusgesti.boulangerie.production.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "perte_production", schema = "boulangerie")
public class PerteProductionEntity {

    @Id
    private UUID id;

    @Column(name = "fournee_id", nullable = false)
    private UUID fourneeId;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(nullable = false)
    private BigDecimal quantite;

    @Column(name = "type_perte", nullable = false)
    private String typePerte;

    private String motif;

    @Column(nullable = false)
    private BigDecimal valorisation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFourneeId() { return fourneeId; }
    public void setFourneeId(UUID fourneeId) { this.fourneeId = fourneeId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public String getTypePerte() { return typePerte; }
    public void setTypePerte(String typePerte) { this.typePerte = typePerte; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public BigDecimal getValorisation() { return valorisation; }
    public void setValorisation(BigDecimal valorisation) { this.valorisation = valorisation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
