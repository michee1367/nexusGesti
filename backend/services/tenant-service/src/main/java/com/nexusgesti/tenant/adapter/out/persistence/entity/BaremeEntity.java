package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bareme", schema = "tenant")
public class BaremeEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "bareme_max", nullable = false)
    private BigDecimal baremeMax;

    @Column(name = "note_minimale", nullable = false)
    private BigDecimal noteMinimale;

    @Column(nullable = false)
    private int precision;

    @Column(nullable = false, length = 20)
    private String arrondi;

    @Column(nullable = false)
    private boolean defaut;

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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getBaremeMax() { return baremeMax; }
    public void setBaremeMax(BigDecimal baremeMax) { this.baremeMax = baremeMax; }
    public BigDecimal getNoteMinimale() { return noteMinimale; }
    public void setNoteMinimale(BigDecimal noteMinimale) { this.noteMinimale = noteMinimale; }
    public int getPrecision() { return precision; }
    public void setPrecision(int precision) { this.precision = precision; }
    public String getArrondi() { return arrondi; }
    public void setArrondi(String arrondi) { this.arrondi = arrondi; }
    public boolean isDefaut() { return defaut; }
    public void setDefaut(boolean defaut) { this.defaut = defaut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}