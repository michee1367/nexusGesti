package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "config_cascade", schema = "socle")
public class ConfigCascadeEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @JdbcTypeCode(SqlTypes.JSON)
    private String niveauxActifs;

    @Column(nullable = false, length = 30)
    private String strategie;

    @Column(nullable = false, length = 20)
    private String arrondi;

    @Column(nullable = false)
    private int precision;

    @Column(name = "seuil_absorption")
    private BigDecimal seuilAbsorption;

    @Column(name = "regle_equivalence_id")
    private UUID regleEquivalenceId;

    @Column(name = "ordre_conversion_lettre", nullable = false)
    private boolean ordreConversionLettre;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getNiveauxActifs() { return niveauxActifs; }
    public void setNiveauxActifs(String niveauxActifs) { this.niveauxActifs = niveauxActifs; }
    public String getStrategie() { return strategie; }
    public void setStrategie(String strategie) { this.strategie = strategie; }
    public String getArrondi() { return arrondi; }
    public void setArrondi(String arrondi) { this.arrondi = arrondi; }
    public int getPrecision() { return precision; }
    public void setPrecision(int precision) { this.precision = precision; }
    public BigDecimal getSeuilAbsorption() { return seuilAbsorption; }
    public void setSeuilAbsorption(BigDecimal seuilAbsorption) { this.seuilAbsorption = seuilAbsorption; }
    public UUID getRegleEquivalenceId() { return regleEquivalenceId; }
    public void setRegleEquivalenceId(UUID regleEquivalenceId) { this.regleEquivalenceId = regleEquivalenceId; }
    public boolean isOrdreConversionLettre() { return ordreConversionLettre; }
    public void setOrdreConversionLettre(boolean ordreConversionLettre) { this.ordreConversionLettre = ordreConversionLettre; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}