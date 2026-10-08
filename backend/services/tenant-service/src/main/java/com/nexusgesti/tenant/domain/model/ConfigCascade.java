package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ConfigCascade {

    private List<NiveauAcalcul> niveauxActifs = new ArrayList<>();
    private StrategieAggregation strategie;
    private TypeArrondi arrondi;
    private int precision;
    private BigDecimal seuilAbsorption;
    private RegleEquivalence regleEquivalence;
    private boolean ordreConversionLettre;

    public BigDecimal agreger(NiveauAcalcul niveau, List<NotePonderee> valeurs) {
        if (!estActif(niveau) || valeurs == null || valeurs.isEmpty()) {
            return null;
        }
        List<NotePonderee> notes = valeurs.stream()
                .filter(n -> n != null && n.getValeur() != null)
                .toList();
        if (notes.isEmpty()) {
            return null;
        }
        BigDecimal resultat = switch (strategie) {
            case MOYENNE_PONDEREE -> moyennePonderee(notes);
            case MOYENNE_NON_PONDEREE -> moyenneSimple(notes);
            case SOMME_PONDEREE -> notes.stream()
                    .map(n -> n.getValeur().multiply(n.getPoids()))
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            case MEILLEURE_NOTE -> notes.stream()
                    .map(NotePonderee::getValeur).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            case DERNIERE_NOTE -> notes.get(notes.size() - 1).getValeur();
            case MOYENNE_TRONQUEE -> moyenneTronquee(notes);
        };
        return arrondi == null ? resultat : arrondi.appliquer(resultat, precision);
    }

    private BigDecimal moyennePonderee(List<NotePonderee> notes) {
        BigDecimal totalPoids = notes.stream()
                .map(NotePonderee::getPoids)
                .filter(p -> p != null)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        if (totalPoids.signum() == 0) {
            return moyenneSimple(notes);
        }
        BigDecimal somme = notes.stream()
                .map(n -> n.getValeur().multiply(n.getPoids()))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return somme.divide(totalPoids, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal moyenneSimple(List<NotePonderee> notes) {
        BigDecimal somme = notes.stream()
                .map(NotePonderee::getValeur)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return somme.divide(BigDecimal.valueOf(notes.size()), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal moyenneTronquee(List<NotePonderee> notes) {
        List<BigDecimal> valeurs = notes.stream().map(NotePonderee::getValeur).sorted().toList();
        if (valeurs.size() >= 4) {
            valeurs = valeurs.subList(1, valeurs.size() - 1);
        } else if (seuilAbsorption != null && seuilAbsorption.signum() > 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal somme = valeurs.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return somme.divide(BigDecimal.valueOf(valeurs.size()), 6, RoundingMode.HALF_UP);
    }

    public BigDecimal normaliser(BigDecimal valeur, Bareme bareme) {
        return bareme == null ? valeur : bareme.normaliser(valeur);
    }

    public boolean estActif(NiveauAcalcul niveau) {
        return niveauxActifs != null && niveau != null && niveauxActifs.contains(niveau);
    }

    public List<NiveauAcalcul> getNiveauxActifs() { return niveauxActifs; }
    public void setNiveauxActifs(List<NiveauAcalcul> niveauxActifs) { this.niveauxActifs = niveauxActifs == null ? new ArrayList<>() : niveauxActifs; }
    public StrategieAggregation getStrategie() { return strategie; }
    public void setStrategie(StrategieAggregation strategie) { this.strategie = strategie; }
    public TypeArrondi getArrondi() { return arrondi; }
    public void setArrondi(TypeArrondi arrondi) { this.arrondi = arrondi; }
    public int getPrecision() { return precision; }
    public void setPrecision(int precision) { this.precision = precision; }
    public BigDecimal getSeuilAbsorption() { return seuilAbsorption; }
    public void setSeuilAbsorption(BigDecimal seuilAbsorption) { this.seuilAbsorption = seuilAbsorption; }
    public RegleEquivalence getRegleEquivalence() { return regleEquivalence; }
    public void setRegleEquivalence(RegleEquivalence regleEquivalence) { this.regleEquivalence = regleEquivalence; }
    public boolean isOrdreConversionLettre() { return ordreConversionLettre; }
    public void setOrdreConversionLettre(boolean ordreConversionLettre) { this.ordreConversionLettre = ordreConversionLettre; }
}