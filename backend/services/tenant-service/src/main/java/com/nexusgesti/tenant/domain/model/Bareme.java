package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Bareme {

    private UUID idBareme;
    private UUID grilleId;
    private String code;
    private String libelle;
    private TypeBareme type;
    private BigDecimal baremeMax;
    private BigDecimal noteMinimale;
    private int precision;
    private TypeArrondi arrondi;
    private boolean defaut;

    public BigDecimal normaliser(BigDecimal valeur) {
        if (valeur == null) {
            return null;
        }
        BigDecimal max = baremeMax == null ? BigDecimal.ZERO : baremeMax;
        BigDecimal bornee = valeur.max(BigDecimal.ZERO).min(max);
        return arrondi == null ? bornee : arrondi.appliquer(bornee, precision);
    }

    public BigDecimal convertir(TypeBareme versType, BigDecimal valeur) {
        BigDecimal from = maxDeType(type);
        BigDecimal to = maxDeType(versType);
        if (from == null || to == null || from.signum() == 0 || valeur == null) {
            return null;
        }
        return valeur.multiply(to).divide(from, precision, RoundingMode.HALF_UP);
    }

    public String convertirVersLettre(BigDecimal valeur) {
        if (valeur == null || baremeMax == null || baremeMax.signum() == 0) {
            return null;
        }
        BigDecimal pct = valeur.divide(baremeMax, 4, RoundingMode.HALF_UP);
        if (pct.compareTo(BigDecimal.valueOf(0.9)) >= 0) return "A";
        if (pct.compareTo(BigDecimal.valueOf(0.75)) >= 0) return "B";
        if (pct.compareTo(BigDecimal.valueOf(0.6)) >= 0) return "C";
        if (pct.compareTo(BigDecimal.valueOf(0.5)) >= 0) return "D";
        return "E";
    }

    public boolean estAtteignable(BigDecimal valeur) {
        if (valeur == null) {
            return false;
        }
        return valeur.signum() >= 0 && (baremeMax == null || valeur.compareTo(baremeMax) <= 0);
    }

    private BigDecimal maxDeType(TypeBareme type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case SUR_5 -> BigDecimal.valueOf(5);
            case SUR_10 -> BigDecimal.valueOf(10);
            case SUR_20 -> BigDecimal.valueOf(20);
            case SUR_100 -> BigDecimal.valueOf(100);
            case POINTS -> baremeMax;
            default -> null;
        };
    }

    public UUID getIdBareme() { return idBareme; }
    public void setIdBareme(UUID idBareme) { this.idBareme = idBareme; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeBareme getType() { return type; }
    public void setType(TypeBareme type) { this.type = type; }
    public BigDecimal getBaremeMax() { return baremeMax; }
    public void setBaremeMax(BigDecimal baremeMax) { this.baremeMax = baremeMax; }
    public BigDecimal getNoteMinimale() { return noteMinimale; }
    public void setNoteMinimale(BigDecimal noteMinimale) { this.noteMinimale = noteMinimale; }
    public int getPrecision() { return precision; }
    public void setPrecision(int precision) { this.precision = precision; }
    public TypeArrondi getArrondi() { return arrondi; }
    public void setArrondi(TypeArrondi arrondi) { this.arrondi = arrondi; }
    public boolean isDefaut() { return defaut; }
    public void setDefaut(boolean defaut) { this.defaut = defaut; }
}