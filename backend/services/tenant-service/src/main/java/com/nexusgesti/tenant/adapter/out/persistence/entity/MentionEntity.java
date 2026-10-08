package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "mention", schema = "tenant")
public class MentionEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 80)
    private String libelle;

    @Column(length = 10)
    private String symbole;

    @Column(name = "seuil_minimum", nullable = false)
    private BigDecimal seuilMinimum;

    @Column(name = "seuil_maximum")
    private BigDecimal seuilMaximum;

    @Column(length = 20)
    private String couleur;

    @Column(name = "appreciation_associee", length = 120)
    private String appreciationAssociee;

    @Column(nullable = false)
    private int ordre;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getSymbole() { return symbole; }
    public void setSymbole(String symbole) { this.symbole = symbole; }
    public BigDecimal getSeuilMinimum() { return seuilMinimum; }
    public void setSeuilMinimum(BigDecimal seuilMinimum) { this.seuilMinimum = seuilMinimum; }
    public BigDecimal getSeuilMaximum() { return seuilMaximum; }
    public void setSeuilMaximum(BigDecimal seuilMaximum) { this.seuilMaximum = seuilMaximum; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public String getAppreciationAssociee() { return appreciationAssociee; }
    public void setAppreciationAssociee(String appreciationAssociee) { this.appreciationAssociee = appreciationAssociee; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}