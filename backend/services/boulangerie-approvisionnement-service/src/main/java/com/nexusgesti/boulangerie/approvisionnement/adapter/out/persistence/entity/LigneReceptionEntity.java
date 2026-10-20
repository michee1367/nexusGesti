package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ligne_reception", schema = "boulangerie_approvisionnement")
public class LigneReceptionEntity {

    @Id
    private UUID id;

    @Column(name = "reception_id", nullable = false)
    private UUID receptionId;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(name = "quantite_commandee", nullable = false)
    private BigDecimal quantiteCommandee;

    @Column(name = "quantite_recue", nullable = false)
    private BigDecimal quantiteRecue;

    @Column(name = "prix_achat", nullable = false)
    private BigDecimal prixAchat;

    @Column(name = "lot_numero")
    private String lotNumero;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(nullable = false)
    private Boolean conforme;

    @Column(name = "motif_non_conformite")
    private String motifNonConformite;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReceptionId() { return receptionId; }
    public void setReceptionId(UUID receptionId) { this.receptionId = receptionId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public BigDecimal getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(BigDecimal quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }
    public BigDecimal getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(BigDecimal quantiteRecue) { this.quantiteRecue = quantiteRecue; }
    public BigDecimal getPrixAchat() { return prixAchat; }
    public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }
    public String getLotNumero() { return lotNumero; }
    public void setLotNumero(String lotNumero) { this.lotNumero = lotNumero; }
    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public Boolean getConforme() { return conforme; }
    public void setConforme(Boolean conforme) { this.conforme = conforme; }
    public String getMotifNonConformite() { return motifNonConformite; }
    public void setMotifNonConformite(String motifNonConformite) { this.motifNonConformite = motifNonConformite; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
