package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "regle_appreciation", schema = "tenant")
public class RegleAppreciationEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 30)
    private String portee;

    @Column(name = "moyenne_minimale", nullable = false)
    private BigDecimal moyenneMinimale;

    @Column(name = "moyenne_maximale")
    private BigDecimal moyenneMaximale;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "modele_texte", length = 500)
    private String modeleTexte;

    @Column(name = "mention_id")
    private UUID mentionId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getPortee() { return portee; }
    public void setPortee(String portee) { this.portee = portee; }
    public BigDecimal getMoyenneMinimale() { return moyenneMinimale; }
    public void setMoyenneMinimale(BigDecimal moyenneMinimale) { this.moyenneMinimale = moyenneMinimale; }
    public BigDecimal getMoyenneMaximale() { return moyenneMaximale; }
    public void setMoyenneMaximale(BigDecimal moyenneMaximale) { this.moyenneMaximale = moyenneMaximale; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getModeleTexte() { return modeleTexte; }
    public void setModeleTexte(String modeleTexte) { this.modeleTexte = modeleTexte; }
    public UUID getMentionId() { return mentionId; }
    public void setMentionId(UUID mentionId) { this.mentionId = mentionId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}