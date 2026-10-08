package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;

/**
 * Valeur ponderee consommee par les strategies d aggregation d une
 * ConfigCascade. Equivalent de la classe pedagogique NotePonderee :
 * un socle ne sait pas agreger des notes sans poids.
 */
public class NotePonderee {

    private BigDecimal valeur;
    private BigDecimal poids;
    private NiveauAcalcul niveau;
    private String typeSourceNote;

    public NotePonderee() {
    }

    public NotePonderee(BigDecimal valeur, BigDecimal poids) {
        this.valeur = valeur;
        this.poids = poids == null ? BigDecimal.ZERO : poids;
    }

    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public BigDecimal getPoids() { return poids; }
    public void setPoids(BigDecimal poids) { this.poids = poids == null ? BigDecimal.ZERO : poids; }
    public NiveauAcalcul getNiveau() { return niveau; }
    public void setNiveau(NiveauAcalcul niveau) { this.niveau = niveau; }
    public String getTypeSourceNote() { return typeSourceNote; }
    public void setTypeSourceNote(String typeSourceNote) { this.typeSourceNote = typeSourceNote; }

    public BigDecimal recalculerPoids(BigDecimal total) {
        if (total == null || total.signum() == 0) {
            this.poids = BigDecimal.ZERO;
            return this.poids;
        }
        this.poids = this.poids.divide(total, 6, java.math.RoundingMode.HALF_UP);
        return this.poids;
    }

    public static NotePonderee of(BigDecimal valeur, BigDecimal poids) {
        return new NotePonderee(valeur, poids);
    }
}