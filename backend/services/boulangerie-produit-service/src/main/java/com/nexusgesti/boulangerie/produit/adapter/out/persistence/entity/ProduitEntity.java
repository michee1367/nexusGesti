package com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "produit", schema = "boulangerie_produit")
public class ProduitEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String designation;

    private String gammeCode;

    private String categorieCode;

    private String articleCode;

    @Column(name = "unite_vente", nullable = false)
    private String uniteVente;

    private Integer poidsUnitaireGrammes;

    @Column(name = "prix_vente_ht", nullable = false)
    private BigDecimal prixVenteHT;

    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTva;

    private String codeBarre;

    private Integer dlcHeures;

    @JdbcTypeCode(SqlTypes.JSON)
    private String allergenes;

    @Column(nullable = false)
    private boolean fabricable;

    @Column(name = "vendable_au_detail", nullable = false)
    private boolean vendableAuDetail;

    @Column(nullable = false)
    private boolean actif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getGammeCode() { return gammeCode; }
    public void setGammeCode(String gammeCode) { this.gammeCode = gammeCode; }
    public String getCategorieCode() { return categorieCode; }
    public void setCategorieCode(String categorieCode) { this.categorieCode = categorieCode; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public String getUniteVente() { return uniteVente; }
    public void setUniteVente(String uniteVente) { this.uniteVente = uniteVente; }
    public Integer getPoidsUnitaireGrammes() { return poidsUnitaireGrammes; }
    public void setPoidsUnitaireGrammes(Integer poidsUnitaireGrammes) { this.poidsUnitaireGrammes = poidsUnitaireGrammes; }
    public BigDecimal getPrixVenteHT() { return prixVenteHT; }
    public void setPrixVenteHT(BigDecimal prixVenteHT) { this.prixVenteHT = prixVenteHT; }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public Integer getDlcHeures() { return dlcHeures; }
    public void setDlcHeures(Integer dlcHeures) { this.dlcHeures = dlcHeures; }
    public String getAllergenes() { return allergenes; }
    public void setAllergenes(String allergenes) { this.allergenes = allergenes; }
    public boolean isFabricable() { return fabricable; }
    public void setFabricable(boolean fabricable) { this.fabricable = fabricable; }
    public boolean isVendableAuDetail() { return vendableAuDetail; }
    public void setVendableAuDetail(boolean vendableAuDetail) { this.vendableAuDetail = vendableAuDetail; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
